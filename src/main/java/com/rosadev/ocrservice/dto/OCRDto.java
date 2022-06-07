package com.rosadev.ocrservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCRDto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRDto {

    private Long idOCR;
    private String documento; //Documento en base 64
    private String nombreDocumento; //Nombre de documento con extensión
    private String idioma; //Idioma que se usara para la lectura
}