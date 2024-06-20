package helloworld;

import java.util.Map;
import com.amazonaws.services.connect.AmazonConnect;
import com.amazonaws.services.connect.AmazonConnectClientBuilder;
import com.amazonaws.services.connect.model.CreateContactFlowRequest;
import com.amazonaws.services.connect.model.CreateContactFlowResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CloudFormationCustomResourceEvent;

/**
 * Handler Contact Flow creation custom resource, after multiple retry I am getting Invalid request Exception. So i have commented out 
 * cfn resource related to this piece
 */
public class ContactFlowPublisher implements RequestHandler<CloudFormationCustomResourceEvent, Map<String, Object>> {


    @Override
    public Map<String, Object> handleRequest(CloudFormationCustomResourceEvent event, Context context) {
        String responseStatus;
        Map<String, Object> responseData = null;
        responseData = createContactFlow(event);
        responseStatus = "SUCCESS";

        return Map.of(
            "Status", responseStatus,
            "PhysicalResourceId", event.getResourceProperties().get("ContactFlowName"),
            "StackId", event.getStackId(),
            "RequestId", event.getRequestId(),
            "LogicalResourceId", event.getLogicalResourceId(),
            "Data", responseData
        );
    }

    private Map<String, Object> createContactFlow(CloudFormationCustomResourceEvent event) {
        AmazonConnect connectClient = AmazonConnectClientBuilder.standard().build();
        String instanceId = event.getResourceProperties().get("InstanceId").toString();
        String contactFlowName = event.getResourceProperties().get("ContactFlowName").toString();
        String contactFlowContent = event.getResourceProperties().get("ContactFlowContent").toString();
        String VanityNumberFucntionArn = (String) event.getResourceProperties().get("VanityNumberFucntionArn");

        contactFlowContent = contactFlowContent.replace("{{VanityNumberFucntionArn}}", VanityNumberFucntionArn);
        contactFlowContent = contactFlowContent.replace("{{ContactFlowName}}", contactFlowName);
        System.out.println(contactFlowContent);
        System.out.println(instanceId);
        System.out.println(VanityNumberFucntionArn);

        CreateContactFlowRequest request = new CreateContactFlowRequest()
            .withInstanceId(instanceId)
            .withName(contactFlowName)
            .withContent(contactFlowContent)
            .withType("CONTACT_FLOW");

        CreateContactFlowResult result = connectClient.createContactFlow(request);

        return Map.of("ContactFlowId", result.getContactFlowId());
    }
}
