package cl.cbrs.indexador.indexador.index;

import cl.cbrs.indexador.indexador.vo.DocumentoCaratulaVO;
import cl.cbrs.indexador.indexador.vo.DocumentoGeneralVO;
import cl.cbrs.indexador.indexador.vo.LiquidacionSueldoDTO;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.apache.http.HttpHost;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.InfoResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import org.opensearch.client.opensearch.OpenSearchClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexaDocumentoCaratula {
    OpenSearchClient client = null;
    AmazonS3 s3Client=null;
    String index = "documentos_caratula_index";
    String BUKCKET="cbrs-escrituras";

    public void configurarIndice() throws IOException {
        //InfoResponse info = client.info();

        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
        client.indices().create(createIndexRequest);
        IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        PutIndicesSettingsRequest putSettingsRequest = new PutIndicesSettingsRequest.Builder()
                .index(index)
                .settings(indexSettings)
                .build();
        client.indices().putSettings(putSettingsRequest);

    }

    public IndexaDocumentoCaratula(String accesKey, String secretKey) {
        try {
            String endpoint = "search-cbrs-documentos-hrjxqmtrrxkojio4ll3vmt72ka.us-east-1.es.amazonaws.com";

            SdkHttpClient httpClient = ApacheHttpClient.builder().build();
            Region region = Region.US_EAST_1;

            client = new OpenSearchClient(
                    new AwsSdk2Transport(
                            httpClient,
                            endpoint,
                            region,
                            AwsSdk2TransportOptions.builder().build()));
            s3Client = new AmazonS3Client(new BasicAWSCredentials(accesKey, secretKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indexar(DocumentoCaratulaVO documento) throws IOException {

        IndexRequest<DocumentoCaratulaVO> indexRequest = new IndexRequest.Builder<DocumentoCaratulaVO>()
                .index(index)
                .id(documento.getId())
                .document(documento)
                .build();
        client.index(indexRequest);
    }
    public void indexar(DocumentoCaratulaVO documento, byte[] archivo) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(archivo.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(archivo);
        String ID=System.currentTimeMillis()+"";
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUKCKET, ID, byteArrayInputStream, metadata);
        s3Client.putObject(putObjectRequest);

        IndexRequest<DocumentoCaratulaVO> indexRequest = new IndexRequest.Builder<DocumentoCaratulaVO>()
                .index(index)
                .id(ID)
                .document(documento)
                .build();
        client.index(indexRequest);
    }

    public List<DocumentoCaratulaVO> getVersiones(DocumentoCaratulaVO documento)throws Exception{
        Query query=  Query.of(qb->{
            qb.bool(BoolQuery.of(bq-> {
                bq.must(f -> f.match(mb -> mb.field("caratula").query(fv -> fv.longValue(documento.getCaratula()))));
                bq.must(f -> f.match(mb -> mb.field("tipoDocumento").query(fv -> fv.stringValue(documento.getTipoDocumento()))));
                return bq;
            }));
            return qb;
        });
        List<DocumentoCaratulaVO> liquidacionSueldoDTOList =new ArrayList<DocumentoCaratulaVO>();
        SearchResponse<DocumentoCaratulaVO> searchResponse = client.search(s -> s.index(index).query(query), DocumentoCaratulaVO.class);
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            DocumentoCaratulaVO liquidacionSueldoDTO1 = searchResponse.hits().hits().get(i).source();
            liquidacionSueldoDTO1.setId(searchResponse.hits().hits().get(i).id());
            liquidacionSueldoDTOList.add(liquidacionSueldoDTO1);
        }
        return liquidacionSueldoDTOList;
    }
    public byte[]  getObject(String key) throws Exception{
        try {
            S3Object s3object = s3Client.getObject(BUKCKET, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
             return bytes;

        } catch (Exception  e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }



}
