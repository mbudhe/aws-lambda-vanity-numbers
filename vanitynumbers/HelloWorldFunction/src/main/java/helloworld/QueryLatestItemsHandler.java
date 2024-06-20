package helloworld;



import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 * This handler latest 5 records from dynamodb and displays it on api, the url or the api has been published as output of cfn
 */

public class QueryLatestItemsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
        .withHeaders(headers);
       AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
       DynamoDB dynamoDB = new DynamoDB(client);
         String tableName = System.getenv("VanityNumberTable"); // Replace with your table name
        LambdaLogger logger = context.getLogger();
        try {
            Table table = dynamoDB.getTable(tableName);
            Index index = table.getIndex("GSI1");

            QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("staticPartition = :v_static")
                .withValueMap(new ValueMap().withString(":v_static", "all"))
                .withScanIndexForward(false) // Order by descending timestamp
                .withMaxResultSize(5);
            //cfetching results from dynamodb using GSI
            ItemCollection<QueryOutcome> items = index.query(querySpec);

            List<Item> result = new ArrayList();
            for (Item item : items) {
                result.add(item);
            }
            // converting result items in HTML to be displayed by api
            String htmlContent = convertItemsToHTML(result);

            Map<String, String> responseheaders = Map.of(
                    "Content-Type", "text/html"
            );
    
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(responseheaders)
                    .withBody(htmlContent);

        } catch (Exception e) {
            logger.log("Error querying items: " + e.getMessage());
            
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }
    private String convertItemsToHTML(List<Item> items) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body><h1>DynamoDB Items</h1><ul>");

        for (Item item : items) {
            htmlBuilder.append("<li>").append(convertItemToHTML(item)).append("</li>");
        }

        htmlBuilder.append("</ul></body></html>");
        return htmlBuilder.toString();
    }

    private String convertItemToHTML(Item item) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Convert DynamoDB item to JSON string
            String json = item.toJSON();
            // Convert JSON string to pretty printed HTML
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return "<pre>" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject) + "</pre>";
        } catch (Exception e) {
            return "<pre>Error converting item to HTML: " + e.getMessage() + "</pre>";
        }
    }
}
