package cl.cbrs.indexador.indexador.vo;

import lombok.Data;

import java.util.Date;
@Data
public class DocumentoCaratulaVO {
    private String id;
    private Long caratula;
    private String nombre;
    private String tipoDocumento;
    private Date fechaDocumento;
    private String buketName;
    private Integer idNotario;
    private String codigoVerificacion;

    private Integer version;

    private String nombreArchivo;




}
