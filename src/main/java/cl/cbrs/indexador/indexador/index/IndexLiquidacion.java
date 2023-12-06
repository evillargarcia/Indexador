package cl.cbrs.indexador.indexador.index;


import cl.cbrs.indexador.indexador.vo.LiquidacionSueldoDTO;
import org.apache.http.HttpHost;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
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

public class IndexLiquidacion {
    org.opensearch.client.opensearch.OpenSearchClient client = null;
    String index = "liquidaciones";

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

    public IndexLiquidacion(String accesKey, String secretKey) {
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

    public void indexar(LiquidacionSueldoDTO liquidacionSueldoDTO) throws IOException {

        IndexRequest<LiquidacionSueldoDTO> indexRequest = new IndexRequest.Builder<LiquidacionSueldoDTO>()
                .index(index)
                .id(liquidacionSueldoDTO.getIdDocumento())
                .document(liquidacionSueldoDTO)
                .build();
        client.index(indexRequest);


    }

    public List<LiquidacionSueldoDTO> buscar(LiquidacionSueldoDTO liquidacionSueldoDTO) throws IOException {


      /*  Query query = QueryBuilders.match().query("ass").field("rut").build();
                .must(QueryBuilders.match().field().operator(BoolQuery.Operator.AND).value(liquidacionSueldoDTO.getRut()).build())
                .must(QueryBuilders.match("anio", liquidacionSueldoDTO.getAnio()));
*/
        /* Query query = Query.of(qb->{
            qb.bool(
                BoolQuery.of(bq->bq.must(f->f.match(mb->mb.field("anio").query(fv->fv.stringValue(liquidacionSueldoDTO.getAnio()))))));
            if (liquidacionSueldoDTO.getRut() != null) {
                qb.bool(BoolQuery.of(bq->bq.must(f->f.match(mb->mb.field("rut").query(fv->fv.stringValue(liquidacionSueldoDTO.getRut()))))));
            }
            return qb;
           });
*/
        Query query=  Query.of(qb->{
            qb.bool(BoolQuery.of(bq-> {
                        bq.must(f -> f.match(mb -> mb.field("anio").query(fv -> fv.stringValue(liquidacionSueldoDTO.getAnio()))));
                        if (liquidacionSueldoDTO.getRut() != null) {
                            bq.must(f -> f.match(mb -> mb.field("rut").query(fv -> fv.stringValue(liquidacionSueldoDTO.getRut()))));
                        }
                if (liquidacionSueldoDTO.getMes() != null) {
                    bq.must(f -> f.match(mb -> mb.field("mes").query(fv -> fv.stringValue(liquidacionSueldoDTO.getMes()))));
                }
                        return bq;
                    }));
                    return qb;
        });

        List<LiquidacionSueldoDTO> liquidacionSueldoDTOList =new ArrayList<LiquidacionSueldoDTO>();
        SearchResponse<LiquidacionSueldoDTO> searchResponse = client.search(s -> s.index(index).query(query), LiquidacionSueldoDTO.class);
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            LiquidacionSueldoDTO liquidacionSueldoDTO1 = searchResponse.hits().hits().get(i).source();
            liquidacionSueldoDTOList.add(liquidacionSueldoDTO1);
        }
        return liquidacionSueldoDTOList;
    }


}
