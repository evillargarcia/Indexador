package cl.cbrs.indexador.indexador.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DocumentoGeneralVO {
    private String id; // con este lo vamos a buscar en el bucket
    private String bucketName;
    private String nombreDocumento;
    private String tipo;
    private String rutEmpleado;
    private String descripcion;
    private Date fechaDocumento;
}
