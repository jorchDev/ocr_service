package com.rosadev.ocrservice.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PeticionOCR
 */



@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeticionOCR {
    public enum Idioma{
        spa;
    }
    
    public  enum TipoArchivo{
        PDF,JPG,PNG;
    }
    

    private String archivoBase64;

    private Idioma idioma;

    private TipoArchivo tipoArchivo;

}