package com.rosadev.ocrservice.service;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rosadev.ocrservice.controller.OCRApiController;
import com.rosadev.ocrservice.dto.OCRResponseDto;
import com.rosadev.ocrservice.dto.PeticionOCR;
import com.rosadev.ocrservice.dto.PeticionOCR.TipoArchivo;
import com.rosadev.ocrservice.lib.FileHandler;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
@Slf4j
public class OCRService {

    @Autowired
    FileHandler fileHandler;

    @Value("${ocr.dpi}")
    private String DPI;

    @Value("${ocr.tesseractLangFile}")
    private String TESSERACT_LANGFILE;
    
    public OCRResponseDto ocrImage(File file, Integer x, Integer y, Integer width, Integer height, Integer bpp) {

        OCRResponseDto ocrResponse = new OCRResponseDto();

        try {

            final Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(TESSERACT_LANGFILE);
            tesseract.setLanguage("spa");
            tesseract.setTessVariable("user_defined_dpi", DPI);

            tesseract.setHocr(false);

            try {
                String texto = "";

                if (file.getName().toUpperCase().endsWith(".PDF")) {
                    texto = tesseract.doOCR(file);
                } else {

                    final BufferedImage ipimage = ImageIO.read(file);

                    String resultadoCOmpleto = "";

                    for (java.awt.Rectangle rect : tesseract.getSegmentedRegions(ipimage, 3)) {
                        // System.out.println("rect : "+rectangle);
                        String res = tesseract.doOCR(ipimage, rect);
                        // System.out.println("res: +"+res);
                        resultadoCOmpleto += res;

                        if (res.toUpperCase().contains("NOMBRE") && rect.getWidth() < 1000 && rect.getHeight() < 420) {
                            System.out.println("Coordenadas Nombre: +" + rect);

                        }

                    }
                    // System.out.println("Texto pageIterator: "+resultadoCOmpleto);

                    if (x != null && y != null && width != null && height != null) {
                        java.awt.Rectangle rectangle = new java.awt.Rectangle(x, y, width, height);
                        texto = tesseract.doOCR(ipimage, rectangle);
                        String nombre = "";
                        String apellidoPaterno = "";
                        String apellidoMaterno = "";

                        StringTokenizer st = new StringTokenizer(texto, "\n");

                        int count = 0;
                        while (st.hasMoreTokens()) {
                            String tok = st.nextToken();
                            System.out.println("token: " + tok);
                            if (!tok.toLowerCase().contains("nombre")) {
                                texto += tok + " ";
                            }

                            count++;
                        }
                    } else {

                        List<java.awt.Rectangle> rects = new ArrayList<java.awt.Rectangle>();
                        rects = tesseract.getSegmentedRegions(ipimage, 0);
                        for (java.awt.Rectangle rectangle : rects) {

                            texto += tesseract.doOCR(ipimage, rectangle);
                            System.out.println("loop - " + rectangle + ":" + texto);
                        }

                        // texto = tesseract.doOCR(ipimage);
                    }
                }
                ocrResponse.setIdOCR(0L);
                ocrResponse.setNombreDocumento(file.getName());
                ;
                ocrResponse.setTextoEncontrado(texto);

               

                return ocrResponse;
            } catch (TesseractException e) {
                e.printStackTrace();

            }
        } catch (Exception e2) {
            log.error(e2.getMessage());

        }
        return ocrResponse;

    }

    private String getFileExtension( TipoArchivo tipoArchivo)
    {
        String ext = "";
        if (tipoArchivo == PeticionOCR.TipoArchivo.PNG) {
            ext = ".png";
        } else if (tipoArchivo == PeticionOCR.TipoArchivo.PDF) {
            ext = ".pdf";
        } else if (tipoArchivo == PeticionOCR.TipoArchivo.JPG) {
            ext = ".jpg";
        }
        return ext;

    }
    public OCRResponseDto ocrImage( PeticionOCR peticionOCR) {

        OCRResponseDto ocrResponse = new OCRResponseDto();
       
        try {
            final File f = new File(
                    this.fileHandler.saveFileBase64(peticionOCR.getArchivoBase64(), "archivo" + this.getFileExtension(peticionOCR.getTipoArchivo()), true));
            final Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(TESSERACT_LANGFILE);
            tesseract.setLanguage(peticionOCR.getIdioma().name());
            tesseract.setTessVariable("user_defined_dpi", DPI);

            tesseract.setHocr(false);
            try {
                String texto = "";
                if (peticionOCR.getTipoArchivo() == PeticionOCR.TipoArchivo.PDF) {
                    texto = tesseract.doOCR(new File(f.getAbsolutePath()));
                } else {
                    final BufferedImage ipimage = ImageIO.read(f);
                    texto = tesseract.doOCR(ipimage);
                }
                ocrResponse.setIdOCR(0L);
                ocrResponse.setNombreDocumento("resultadoOCR");
                ;
                ocrResponse.setTextoEncontrado(texto);

        

                return ocrResponse;
            } catch (TesseractException e) {
                e.printStackTrace();

            }
        } catch (Exception e2) {
            log.error("Error: "+e2.getMessage());

        }
        return ocrResponse;

    }

}
