package exp.evalidea;


import java.util.*;

public class DataProcessUtil {

    public static HashMap<String,String> resolveValueBag(String variableLine) {
//        f0e1b75fb9632a50cf37307630493b9780a26bb0###/addthis_stream-lib/src/test/java/com/clearspring/analytics/stream/cardinality/TestCountThenEstimate.java###/TestCountThenEstimate.java###com.clearspring.analytics.stream.cardinality.TestCountThenEstimate###assertCountThenEstimateEquals:CountThenEstimate CountThenEstimate ###assertArrayEquals(expected.estimator.getBytes(),actual.estimator.getBytes());###expBytes###expected.estimator.getBytes()###256:13:256:91
        // commitID+"###"+javaFilePath +"###" javaFileName +"###" + classPath + "###" + methodInfo +"###" + involvedExpression +"###" + variableName+"###" + initializer +"###" + offset;
        String[] splitArray = variableLine.replaceAll("\n", "").split("###");
        HashMap<String, String> values = new HashMap<>();
        if (splitArray.length != 9) {
            System.out.println("not valid record <8");
            return null;
        } else {
            String commitID = splitArray[0];
            values.put("commitID", commitID);
            String javaFileName = splitArray[2];
            values.put("javaFileName", javaFileName);
            String javaFilePath = splitArray[1];
            values.put("javaFilePath", javaFilePath);
            String classPath = splitArray[3];
            values.put("classPath", classPath);
            String methodInfo = splitArray[4];
            values.put("methodInfo", methodInfo);
            String involvedExpression = splitArray[5];
            values.put("involvedExpression", involvedExpression);
            String variableName = splitArray[6];
            values.put("variableName", variableName);
            String initializer = splitArray[7];
            values.put("initializer", initializer);
            String lineAndColumn = splitArray[8];
            values.put("lineAndColumn", lineAndColumn);
        }
        return values;
    }
}


