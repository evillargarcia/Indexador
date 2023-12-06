package cl.cbrs.indexador.indexador.vo;

import lombok.Data;

@Data
public class LiquidacionSueldoDTO {
    private String numeroEmpleado;
    private String nombre;
    private String email;
    private String mes;
    private String anio;
    private Boolean procesado;
    private String fechaProceso;
    private String rut;
    private String registro;
    private String tipoDocumento;
    private String idDocumento;


}
