package cl.cbrs.indexador.indexador;

import cl.cbrs.indexador.indexador.index.IndexaDocumentoCaratula;
import cl.cbrs.indexador.indexador.vo.DocumentoCaratulaVO;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TESTConsulta {
    public static void  main(String[] args) throws Exception {
        DocumentoCaratulaVO consulta= new DocumentoCaratulaVO();
        consulta.setCaratula(20815989l);
        consulta.setTipoDocumento("ESCRITURA");
        IndexaDocumentoCaratula index= new IndexaDocumentoCaratula("AKIARAJW6HPDKXI3DP7F","lrq6HIwwMJ6oSwKhm8F2EhWscUAYk9Y7IhuUpQUP");
        List<DocumentoCaratulaVO> lita=index.getVersiones(consulta);
        for (int j=0;j<lita.size();j++){
            System.out.println(lita.get(j).getCaratula()+" -"+lita.get(j).getFechaDocumento()+" - "+lita.get(j).getNombre());
            try (FileOutputStream fos = new FileOutputStream("C:/pdf/"+lita.get(j).getNombre())) {
                fos.write(index.getObject(lita.get(j).getId()));
                //fos.close // no need, try-with-resources auto close
            }

        }
    }
}
