package cl.cbrs.indexador.indexador;

import cl.cbrs.indexador.indexador.index.IndexFirmaPerito;
import cl.cbrs.indexador.indexador.index.IndexLiquidacion;

import cl.cbrs.indexador.indexador.index.IndexadorGenerico;
import cl.cbrs.indexador.indexador.vo.DocumentoGeneralVO;
import cl.cbrs.indexador.indexador.vo.DocumentoPeritoVO;
import cl.cbrs.indexador.indexador.vo.LiquidacionSueldoDTO;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Test {
    public static void main(String[] args) throws IOException {

        IndexadorGenerico openSearchClient = new IndexadorGenerico("AKIARAJW6HPDKXI3DP7F","lrq6HIwwMJ6oSwKhm8F2EhWscUAYk9Y7IhuUpQUP");
        //openSearchClient.configurarIndice();
        DocumentoGeneralVO documentoGeneralVO = new DocumentoGeneralVO();
        documentoGeneralVO.setBucketName("cbrs-etrus");
        documentoGeneralVO.setNombreDocumento("test");
        documentoGeneralVO.setTipo("Licencia Medica");
        documentoGeneralVO.setRutEmpleado("13836929-3");
        documentoGeneralVO.setDescripcion("test");
        documentoGeneralVO.setFechaDocumento(new Date());
        documentoGeneralVO.setId("test");
        openSearchClient.indexar(documentoGeneralVO);
    }
}

