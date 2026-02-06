package com.tracebill.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.tracebill.module.batch.entity.Batch;
import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.party.entity.BillingEntity;
import com.tracebill.module.party.service.BillingEntityService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InvoicePDFGenerator {

	private final BillingEntityService billingEntityService;
	private final ProductService productService;
	private final BatchService batchService;
	
	public byte[] createInvoicePDF(Invoice invoice) {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(baos);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    document.setMargins(36, 36, 36, 36);

	    // ================= HEADER =================
	    document.add(
	        new Paragraph("TAX INVOICE")
	            .setBold()
	            .setFontSize(18)
	            .setTextAlignment(TextAlignment.CENTER)
	    );

	    document.add(new LineSeparator(new SolidLine()));

	    // ================= INVOICE META =================
	    Table meta = new Table(new float[]{70, 30}).useAllAvailableWidth();

	    meta.addCell(new Cell().setBorder(Border.NO_BORDER));
	    meta.addCell(
	        new Cell()
	            .add(new Paragraph("Invoice No : " + invoice.getInvoiceNo()))
	            .add(new Paragraph("Date : " + invoice.getDate()))
	            .setTextAlignment(TextAlignment.RIGHT)
	            .setBorder(Border.NO_BORDER)
	    );

	    document.add(meta);
	    document.add(new Paragraph("\n"));

	    // ================= PARTY DETAILS =================
	    BillingEntity from = billingEntityService
	            .getBillingEntityByBillingEntityId(invoice.getFromBillingEntityId());

	    BillingEntity to = billingEntityService
	            .getBillingEntityByBillingEntityId(invoice.getToBillingEntityId());

	    Table party = new Table(new float[]{50, 50}).useAllAvailableWidth();

	    party.addHeaderCell(boldCell("Consignor Details"));
	    party.addHeaderCell(boldCell("Consignee Details"));

	    party.addCell(detailCell(from));
	    party.addCell(detailCell(to));

	    document.add(party);
	    document.add(new Paragraph("\n"));

	    // ================= ITEM TABLE =================
	    Table table = new Table(new float[]{
	        1, 4, 2, 1.5f, 1.5f, 1.5f, 2, 2, 1.5f, 2
	    }).useAllAvailableWidth();

	    String[] headers = {
	        "#", "Product", "HSN", "Qty", "GST%", "CESS%",
	        "Rate", "Tax", "Disc%", "Amount"
	    };

	    for (String h : headers) {
	        table.addHeaderCell(
	            boldCell(h).setTextAlignment(TextAlignment.CENTER)
	        );
	    }

	    int i = 1;
	    BigDecimal cgst = BigDecimal.ZERO;
	    BigDecimal sgst = BigDecimal.ZERO;
	    BigDecimal cess = BigDecimal.ZERO;

	    for (InvoiceItem item : invoice.getItems()) {

	        Product product = productService.getProductById(item.getProductId());

	        BigDecimal qty = new BigDecimal(item.getQty());
	        BigDecimal gross = item.getRate().multiply(qty);
	        BigDecimal discAmt = gross
	                .multiply(item.getDisc())
	                .divide(BigDecimal.valueOf(100));

	        BigDecimal net = gross.subtract(discAmt);

	        BigDecimal gstRate = product.getGstRate();
	        BigDecimal cessRate = product.getCessRate();

	        BigDecimal gstAmt = net.multiply(gstRate).divide(BigDecimal.valueOf(100));
	        BigDecimal cessAmt = net.multiply(cessRate).divide(BigDecimal.valueOf(100));

	        table.addCell(cell(String.valueOf(i++)));
	        table.addCell(cell(product.getProdName()));
	        table.addCell(cell(product.getHsnCode()));
	        table.addCell(cell(qty.toPlainString()));
	        table.addCell(cell(gstRate.toPlainString()));
	        table.addCell(cell(cessRate.toPlainString()));
	        table.addCell(cell(formatAmount(net.divide(qty).floatValue())));
	        table.addCell(cell(formatAmount(gstAmt.floatValue())));
	        table.addCell(cell(item.getDisc().toPlainString()));
	        table.addCell(cell(formatAmount(net.add(gstAmt).add(cessAmt).floatValue())));

	        cgst = cgst.add(gstAmt.divide(BigDecimal.valueOf(2)));
	        sgst = sgst.add(gstAmt.divide(BigDecimal.valueOf(2)));
	        cess = cess.add(cessAmt);
	    }

	    document.add(table);
	    document.add(new Paragraph("\n"));

	    // ================= TOTALS (RIGHT ALIGNED) =================
	    BigDecimal roundedTotal = invoice.getTotalAmt().setScale(0, RoundingMode.HALF_UP);
	    BigDecimal roundOff = roundedTotal.subtract(invoice.getTotalAmt());

	    Table totals = new Table(new float[]{70, 30}).useAllAvailableWidth();

	    totals.addCell(totalLabel("Subtotal"));
	    totals.addCell(totalValue(invoice.getAmt()));

	    totals.addCell(totalLabel("Tax"));
	    totals.addCell(totalValue(invoice.getTax()));

	    totals.addCell(totalLabel("Round Off"));
	    totals.addCell(totalValue(roundOff));

	    totals.addCell(totalLabel("Grand Total").setBold());
	    totals.addCell(totalValue(roundedTotal).setBold());

	    document.add(totals);

	    // ================= AMOUNT IN WORDS =================
	    document.add(new Paragraph("\nTotal Payable Amount (in words):").setBold());
	    document.add(new Paragraph(convertRupeesToWords(roundedTotal.floatValue())));

	    // ================= TAX SUMMARY =================
	    document.add(new Paragraph("\nTax Summary").setBold());

	    Table tax = new Table(new float[]{25, 25, 25, 25}).useAllAvailableWidth();

	    tax.addHeaderCell(boldCell("CGST"));
	    tax.addHeaderCell(boldCell("SGST"));
	    tax.addHeaderCell(boldCell("CESS"));
	    tax.addHeaderCell(boldCell("Total Tax"));

	    BigDecimal totalTax = cgst.add(sgst).add(cess);

	    tax.addCell(cell(formatAmount(cgst.floatValue())));
	    tax.addCell(cell(formatAmount(sgst.floatValue())));
	    tax.addCell(cell(formatAmount(cess.floatValue())));
	    tax.addCell(cell(formatAmount(totalTax.floatValue())));

	    document.add(tax);

	    document.add(new Paragraph("\nTotal Tax Amount (in words):").setBold());
	    document.add(new Paragraph(convertRupeesToWords(totalTax.floatValue())));

	    document.close();
	    return baos.toByteArray();
	}





	// ------------------ Helper Methods ------------------

	private static Cell cell(String text) {
		return new Cell().add(new Paragraph(text)).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.CENTER);
	}

	private static Cell boldCell(String text) {
		return new Cell().add(new Paragraph(text).setBold());
	}

	private static Cell empty() {
		return new Cell().add(new Paragraph());
	}

	private static Cell detailCell(BillingEntity entity) {
		return new Cell().add(new Paragraph("Name : " + entity.getLegalName())).add(new Paragraph("GST : " + entity.getGstNo()))
				.add(new Paragraph("Phone No. : " + entity.getPhoneNumber())).add(new Paragraph("Address : " + entity.getAddress()))
				.add(new Paragraph("Email : " + entity.getEmailId()));
	}

	private static String formatAmount(float amount) {
		NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		return format.format(amount);
	}

	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	private static String convertBelowThousand(int number) {
		String result = "";
		if (number >= 100) {
			result += units[number / 100] + " Hundred ";
			number %= 100;
		}
		if (number >= 20) {
			result += tens[number / 10] + " ";
			number %= 10;
		}
		if (number > 0) {
			result += units[number] + " ";
		}
		return result.trim();
	}

	private static String convertNumberToWords(int number) {
		if (number == 0)
			return "Zero";

		StringBuilder sb = new StringBuilder();

		if (number / 10000000 > 0) {
			sb.append(convertBelowThousand(number / 10000000)).append(" Crore ");
			number %= 10000000;
		}
		if (number / 100000 > 0) {
			sb.append(convertBelowThousand(number / 100000)).append(" Lakh ");
			number %= 100000;
		}
		if (number / 1000 > 0) {
			sb.append(convertBelowThousand(number / 1000)).append(" Thousand ");
			number %= 1000;
		}
		if (number > 0) {
			sb.append(convertBelowThousand(number));
		}

		return sb.toString().trim();
	}

	public static String convertRupeesToWords(float amount) {
		int rupees = (int) amount;
		int paise = Math.round((amount - rupees) * 100);

		String rupeePart = convertNumberToWords(rupees) + " Rupees";
		String paisePart = (paise > 0) ? " and " + convertNumberToWords(paise) + " Paise" : "";

		return rupeePart + paisePart + " Only";
	}
	
	private Cell totalLabel(String text) {
	    return new Cell()
	            .add(new Paragraph(text))
	            .setBorder(Border.NO_BORDER)
	            .setTextAlignment(TextAlignment.RIGHT)
	            .setPaddingRight(10);
	}

	
	private Cell totalValue(BigDecimal value) {
	    return new Cell()
	            .add(new Paragraph(formatAmount(value.floatValue())))
	            .setBorder(Border.NO_BORDER)
	            .setTextAlignment(TextAlignment.RIGHT);
	}




}
