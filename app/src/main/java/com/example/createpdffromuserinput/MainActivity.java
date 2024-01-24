package com.example.createpdffromuserinput;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.Border3D;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText name, fname, stdclass, email, fees;
    Button btn1;

    DateTimeFormatter dateFormatter,timeFormatter;
    BarcodeQRCode barcodeQRCode;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById (R.id.Name);
        fname = findViewById (R.id.Fname);
        stdclass = findViewById (R.id.Class);
        email = findViewById (R.id.Email);
        fees = findViewById (R.id.Fees);
        btn1 = findViewById (R.id.Done);

        btn1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String usernmae = name.getText ().toString ();
                String fname1 = fname.getText ().toString ();
                String email1 = email.getText ().toString ();
                String fees1 = fees.getText ().toString ();
                String class1 = stdclass.getText ().toString ();

                try {
                    createPdf (usernmae,fname1,email1,fees1,class1);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException (e);
                }


            }
        });
    }
    private void createPdf(String username_, String fname_, String Email_,String fees,String class1) throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Feesvoucher.pdf");
        OutputStream outputStream = new FileOutputStream (file);

        PdfWriter writer = new PdfWriter(file);
        com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new PdfDocument (writer);
        com.itextpdf.layout.Document document = new Document (pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0, 0, 0, 0);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getDrawable(R.drawable.smiulogo);
        }
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        com.itextpdf.io.source.ByteArrayOutputStream stream = new ByteArrayOutputStream ();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image (imageData);

        Paragraph visitor_ticket = new Paragraph("Habib Bank Limited")
                .setBold()
                .setFontSize(17)
                .setFontColor(new DeviceRgb (255,255,255))
                .setBackgroundColor(ColorConstants.BLACK)
                .setMargins(8,18,8,18)
                .setBorder(Border.NO_BORDER)
                .setBorderRadius(new BorderRadius (20))
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph group = new Paragraph("HBL Account "+"#004..........xyz")
                .setFontSize(10)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        Paragraph varanasi = new Paragraph("Please submit your ad fee and then upload the photo of this voucher")
                .setBold()
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);

        float[] width = {120f, 20f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addCell(new Cell ().add(new Paragraph("Name")));
        table.addCell(new Cell().add(new Paragraph(username_)));



        table.addCell(new Cell().add(new Paragraph("Father name")));
        table.addCell(new Cell().add(new Paragraph(fname_)));

        table.addCell(new Cell().add(new Paragraph("Email")));
        table.addCell(new Cell().add(new Paragraph(Email_)));
        table.addCell(new Cell().add(new Paragraph("Class")));
        table.addCell(new Cell().add(new Paragraph(class1)));
        table.addCell(new Cell().add(new Paragraph("Total Fees")));
        table.addCell(new Cell().add(new Paragraph(fees)));

        table.addCell(new Cell().add(new Paragraph("Date")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateFormatter))));
        }

        table.addCell(new Cell().add(new Paragraph("Time")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");    //for 24 hour format use HH
            table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter))));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            barcodeQRCode = new BarcodeQRCode (username_+"\n"+fname_+"\n"+Email_+"\n"+fees+"\n"+LocalDate.now().format(dateFormatter)+"\n"+LocalTime.now().format(timeFormatter));
        }

        PdfFormXObject pdfFormXObject = barcodeQRCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        com.itextpdf.layout.element.Image barcodeimage = new Image (pdfFormXObject)
                .setWidth(80)
                .setHeight(80)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        document.add(image);
        document.add(visitor_ticket);
        document.add(group);
        document.add(varanasi);
        document.add(table);
        document.add(barcodeimage);

        document.close();
        Toast.makeText (this, "your Fee Voucher has been downloaded", Toast.LENGTH_LONG).show ();

    }

}