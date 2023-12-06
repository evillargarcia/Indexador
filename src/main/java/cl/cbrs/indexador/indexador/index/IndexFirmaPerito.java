package cl.cbrs.indexador.indexador.index;


import cl.cbrs.indexador.indexador.vo.DocumentoPeritoVO;
import cl.cbrs.indexador.indexador.vo.LiquidacionSueldoDTO;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexFirmaPerito {
    org.opensearch.client.opensearch.OpenSearchClient client = null;
    String index = "firma_perito";

   public void configurarIndice() throws IOException {
       InfoResponse info = client.info();

       CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
       client.indices().create(createIndexRequest);
       IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
       PutIndicesSettingsRequest putSettingsRequest = new PutIndicesSettingsRequest.Builder()
               .index(index)
               .settings(indexSettings)
               .build();
       client.indices().putSettings(putSettingsRequest);



   }

    public IndexFirmaPerito(String accesKey, String secretKey) {
        try {
            Region region = Region.US_EAST_1;
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                    accesKey,secretKey);
            String endpoint = "https://search-cbrs-documentos-hrjxqmtrrxkojio4ll3vmt72ka.us-east-1.es.amazonaws.com";  // or the endpoint for the correct region
            AwsCredentialsProvider credentialProvider = StaticCredentialsProvider.create(awsCreds);
            SdkHttpClient httpClient = ApacheHttpClient.builder().build();
            AwsSdk2TransportOptions awsOption = AwsSdk2TransportOptions.builder().setCredentials(credentialProvider).build();
            client = new org.opensearch.client.opensearch.OpenSearchClient(
                    new AwsSdk2Transport(
                            httpClient,
                            HttpHost.create(endpoint).getHostName(),
                            region,
                            awsOption));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indexar(DocumentoPeritoVO documentoPeritoVOrito) throws IOException {

        IndexRequest<DocumentoPeritoVO> indexRequest = new IndexRequest.Builder<DocumentoPeritoVO>()
                .index(index)
                .id(documentoPeritoVOrito.getIdDocumento())
                .document(documentoPeritoVOrito)
                .build();
        client.index(indexRequest);


    }

    public List<DocumentoPeritoVO> buscar(DocumentoPeritoVO documentoPeritoVO) throws IOException {
        Query query=  Query.of(qb->{
            qb.bool(BoolQuery.of(bq-> {
                        bq.must(f -> f.match(mb -> mb.field("descripcion").query(fv -> fv.stringValue(documentoPeritoVO.getDescripcion()))));
                       return bq;
                    }));
                    return qb;
         });
         List<DocumentoPeritoVO> documentosList =new ArrayList<DocumentoPeritoVO>();
        SearchResponse<DocumentoPeritoVO> searchResponse = client.search(s -> s.index(index).query(query), DocumentoPeritoVO.class);
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            DocumentoPeritoVO liquidacionSueldoDTO1 = searchResponse.hits().hits().get(i).source();
            documentosList.add(liquidacionSueldoDTO1);
        }
        return documentosList;
    }

    public List<DocumentoPeritoVO> autoCompletar(DocumentoPeritoVO documentoPeritoVO) throws IOException {
        Query query=  Query.of(qb->
            qb.matchPhrasePrefix(f->f.field("descripcion").query(documentoPeritoVO.getDescripcion()).slop(3).maxExpansions(10)));

        List<DocumentoPeritoVO> documentosList =new ArrayList<DocumentoPeritoVO>();
        SearchResponse<DocumentoPeritoVO> searchResponse = client.search(s -> s.index(index).query(query), DocumentoPeritoVO.class);
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            DocumentoPeritoVO liquidacionSueldoDTO1 = searchResponse.hits().hits().get(i).source();
            documentosList.add(liquidacionSueldoDTO1);
        }
        return documentosList;
    }


}
