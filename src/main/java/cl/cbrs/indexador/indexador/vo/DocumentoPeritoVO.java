package cl.cbrs.indexador.indexador.vo;

import lombok.Data;

import java.util.Date;
@Data
public class DocumentoPeritoVO {
    private String idDocumento;
    private String nombre;
    private String descripcion;
    private Date fechaVersion;
    private int estado;
    private int version;

}
