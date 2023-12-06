package cl.cbrs.indexador.indexador;

import cl.cbrs.indexador.indexador.index.IndexaDocumentoCaratula;
import cl.cbrs.indexador.indexador.vo.DocumentoCaratulaVO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

public class TestIndexaCaratula {
    public static void  main(String[] args) throws Exception {
        System.out.println("Hola Mundo");
        File directorio=new File("//srv-nas4.cbrs.local/DescargaEscrituraElectronica/ESCRITURAS/TEMP_AWS");
        File[] file=directorio.listFiles();
        IndexaDocumentoCaratula index= new IndexaDocumentoCaratula("AKIARAJW6HPDKXI3DP7F","lrq6HIwwMJ6oSwKhm8F2EhWscUAYk9Y7IhuUpQUP");
        //index.configurarIndice();
        for(int i=0;i<file.length;i++){
                File unArchivo=file[i];
                String caratula=unArchivo.getName().split("_")[1];

                caratula=caratula.substring(0,caratula.indexOf('.'));
            System.out.println(caratula);
            DocumentoCaratulaVO caratulaVO= new DocumentoCaratulaVO();
            caratulaVO.setCaratula(new Long(caratula));
            caratulaVO.setTipoDocumento("ESCRITURA");
            caratulaVO.setFechaDocumento(new Date());
            caratulaVO.setNombre(unArchivo.getName());
            byte[] fileBytes1 = Files.readAllBytes(unArchivo.toPath());
            index.indexar(caratulaVO,fileBytes1);
            unArchivo.delete();




        }


    }
}
