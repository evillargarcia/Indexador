package cl.cbrs.indexador.indexador.index;

import cl.cbrs.indexador.indexador.vo.DocumentoGeneralVO;
import cl.cbrs.indexador.indexador.vo.DocumentoPeritoVO;
import cl.cbrs.indexador.indexador.vo.LiquidacionSueldoDTO;
import org.apache.http.HttpHost;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.InfoResponse;
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

public class IndexadorGenerico {
    org.opensearch.client.opensearch.OpenSearchClient client = null;
    String index = "documentos_index";

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

    public IndexadorGenerico(String accesKey, String secretKey) {
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
    public void indexar(DocumentoGeneralVO liquidacionSueldoDTO) throws IOException {

        IndexRequest<DocumentoGeneralVO> indexRequest = new IndexRequest.Builder<DocumentoGeneralVO>()
                .index(index)
                .id(liquidacionSueldoDTO.getId())
                .document(liquidacionSueldoDTO)
                .build();
        client.index(indexRequest);
    }
}
