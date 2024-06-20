package helloworld;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.ConnectEvent;

/**
 * Handler for suggesting vanity numbers, based on caller number
 */
@SuppressWarnings("rawtypes")
public class SuggestVanityNumber implements RequestHandler<ConnectEvent, ConnectResponse>{
    //Mapping Digits to Characters:
    private static final Map<Character, String> DIGIT_TO_CHARS = new HashMap<>();
    {
   DIGIT_TO_CHARS.put('2', "ABC");
   DIGIT_TO_CHARS.put('3', "DEF");
   DIGIT_TO_CHARS.put('4', "GHI");
   DIGIT_TO_CHARS.put('5', "JKL");
   DIGIT_TO_CHARS.put('6', "MNO");
   DIGIT_TO_CHARS.put('7', "PQRS");
   DIGIT_TO_CHARS.put('8', "TUV");
   DIGIT_TO_CHARS.put('9', "WXYZ");
}
@Override
public ConnectResponse handleRequest(ConnectEvent event, Context context) {
    String tableName = System.getenv("VanityNumberTable");
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    DynamoDB dynamoDB = new DynamoDB(client);
    Table table = dynamoDB.getTable(tableName);
    long currentTimestamp = Instant.now().getEpochSecond();
    ConnectResponse response = new ConnectResponse();

       // String phoneNumber = "123456789";
       Map<String, Object> parameters = event.getDetails().getParameters();
       String phoneNumber = parameters.get("CustomerNumber").toString();
       if (phoneNumber != null && phoneNumber.startsWith("+")) {
        phoneNumber = phoneNumber.replace("+", "");
    }
       
        List<String> vanityNumbers = getVanityNumbers(phoneNumber);
          table.putItem(new Item()
                .withPrimaryKey("customernumber", phoneNumber)
                .withList("VanityNumbers", vanityNumbers)
                .withNumber("timestamp", currentTimestamp)
                .withString("staticPartition", "all"));

                Map<String, String> outputMap = new HashMap<>();
                outputMap.put("VanityNumber1", vanityNumbers.get(0));
                outputMap.put("VanityNumber2", vanityNumbers.get(1));
                outputMap.put("VanityNumber3", vanityNumbers.get(2));
                response.setParameters(outputMap);
                return response;

    }

   private List<String> getVanityNumbers(String phoneNumber) {
        List<String> results = new ArrayList<>();
        generateVanityNumbers(phoneNumber, 0, new StringBuilder(), results);
        
        results.sort(Comparator.comparingLong(this::countVowels).reversed());
        return results.size() > 5 ? results.subList(0, 5) : results;
    }
/*
 * The function utilizes a recursive approach to generate all possible combinations of vanity numbers based on the input phone number.
At each step of the recursion, the function appends characters corresponding to the current digit in the phone number (or the digit itself if no corresponding characters are found) to the current string, and then calls itself recursively with the next index.
After the recursive call, the appended character is removed from the current string to backtrack and explore other possibilities.
 */
    private void generateVanityNumbers(String phoneNumber, int index, StringBuilder current, List<String> results) {
        if (index == phoneNumber.length()) {
            results.add(current.toString());
            return;
        }

        char digit = phoneNumber.charAt(index);
        String letters = DIGIT_TO_CHARS.get(digit);
        
        if (letters != null) {
            for (char letter : letters.toCharArray()) {
                current.append(letter);
                generateVanityNumbers(phoneNumber, index + 1, current, results);
                current.deleteCharAt(current.length() - 1);
            }
        } else {
            current.append(digit);
            generateVanityNumbers(phoneNumber, index + 1, current, results);
            current.deleteCharAt(current.length() - 1);
        }
    }
    //The countVowels method counts the number of vowels in a string, used for sorting.
    /*
     * After generating all possible vanity numbers, the function sorts them based on the count of vowels in each string.
Vanity numbers with a higher count of vowels are considered more desirable as they tend to be more memorable and easier to pronounce.
Sorting by vowel count ensures that the most appealing vanity numbers are presented first to the user.
     */
    private long countVowels(String s) {
        return s.chars().filter(c -> "AEIOU".indexOf(c) != -1).count();
    }
}

class ConnectResponse {
    private Map<String, String> parameters;

    public ConnectResponse() {}

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
