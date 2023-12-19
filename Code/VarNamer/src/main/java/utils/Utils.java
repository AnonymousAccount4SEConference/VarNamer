package utils;

import edu.lu.uni.serval.utils.FileHelper;


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.DataProcessUtil.resolveValueBag;


public class Utils {
    public static void main(String [] args) throws FileNotFoundException {
        String loc = "C:\\Users\\25359\\Desktop\\fsdownload\\LOC.txt";
        String complexities = "C:\\Users\\25359\\Desktop\\fsdownload\\complexities.txt";
        ArrayList<String> loc_list = FileHelper.readFileByLines(loc);
        ArrayList<String> com_list = FileHelper.readFileByLines(complexities);
        System.out.println(frequencyOfList(loc_list));
        System.out.println(frequencyOfList(com_list));
    }

    //    public static List<CoreMap> splitWord(String sample){
//        Properties props = new Properties();
//
//        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");    // 七种Annotators
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);    // 依次处理
//
//        String text = "This is a test.";               // 输入文本
//
//        Annotation document = new Annotation(text);    // 利用text创建一个空的Annotation
//        pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）
//
//        // 下面的sentences 中包含了所有分析结果，遍历即可获知结果。
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//        return sentences;
//    }
    public static void addIfNotNull(Collection c, Collection c1){
        if(c1!=null) c.addAll(c1);
    }

    public  static void groupByKey(String keyName, String dataFilePath, String outputPath,String projectName) throws FileNotFoundException {
        ArrayList<String> lines= FileHelper.readFileByLines(dataFilePath);
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        for(String line:lines){
            HashMap<String, String> valueMap = resolveValueBag(line);
            assert valueMap != null;
            String key = valueMap.get(keyName);
            if(map.containsKey(key)){
                ArrayList<String> list= map.get(key);
                list.add(line);
                map.put(key,list);
            }
            else{
                ArrayList<String> list= new ArrayList<>();
                list.add(line);
                map.put(key,list);
            }
        }
        StringBuilder stringBuilder= new StringBuilder();
        for(Map.Entry<String,ArrayList<String>> entry:map.entrySet()){
            ArrayList<String> list= entry.getValue();
            for(String s:list){
                stringBuilder.append(s).append("\n");
            }
        }
        FileHelper.outputToFile(outputPath+ projectName+".txt",stringBuilder,false);
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    public static List<String> splitVariableName(String variableName) {
        List<String> tokens = new ArrayList<>();

        // Split based on camel case
        Pattern camelCasePattern = Pattern.compile("(?<=[a-z])(?=[A-Z])");
        String[] camelCaseParts = camelCasePattern.split(variableName);

        for (String part : camelCaseParts) {
            // Split each part based on underscores
//            String[] underscoreParts = part.split("_");
            String[] underscoreParts = part.split("[_,.<>\\[\\]]");
            for (String underscorePart : underscoreParts) {
                tokens.add(underscorePart.toLowerCase());
            }
        }

        return tokens;
    }
    public static void copyFile(File source, String dest,String newName )throws IOException {
        //创建目的地文件夹
        File destfile = new File(dest);
        if(!destfile.exists()){
            destfile.mkdir();
        }
        //如果source是文件夹，则在目的地址中创建新的文件夹
        if(source.isDirectory()){
            File file = new File(dest+"\\"+source.getName());//用目的地址加上source的文件夹名称，创建新的文件夹
            file.mkdir();
            //得到source文件夹的所有文件及目录
            File[] files = source.listFiles();
            if(files.length==0){
                return;
            }else{
                for(int i = 0 ;i<files.length;i++){
                    copyFile(files[i],file.getPath(),newName);
                }
            }

        }
        //source是文件，则用字节输入输出流复制文件
        else if(source.isFile()){
            FileInputStream fis = new FileInputStream(source);
            //创建新的文件，保存复制内容，文件名称与源文件名称一致
            File dfile = new File(dest+"\\"+newName);
            if(!dfile.exists()){
                dfile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(dfile);
            // 读写数据
            // 定义数组
            byte[] b = new byte[1024];
            // 定义长度
            int len;
            // 循环读取
            while ((len = fis.read(b))!=-1) {
                // 写出数据
                fos.write(b, 0 , len);
            }

            //关闭资源
            fos.close();
            fis.close();

        }
    }
    public static void ifExistsDuplicates(List<Integer> list){
        HashMap<Integer,Integer> hashMap=new HashMap<Integer, Integer>();
        for(Integer i:list){
            if(hashMap.get(i)!=null){  //hashMap包含遍历list中的当前元素
                Integer integer=hashMap.get(i);
                hashMap.put(i,integer+1);
                System.out.println("the element:"+i+" is repeat");
            }
            else{
                hashMap.put(i,1);
            }
        }
    }

    public static ArrayList<Integer> getRandomNumbers(int bound, int seed,int number){
        Random random = new Random(seed);
        int rand = 0;
        ArrayList<Integer> selectedNumbers = new ArrayList<>();
        for(int i=0;i<number;i++){
            rand = random.nextInt(bound);
            while(selectedNumbers.contains(rand)){
                rand = random.nextInt(bound);
            }
            selectedNumbers.add(rand);
        }
//        System.out.println(selectedNumbers);
        return selectedNumbers;
    }
    public static Map<String, Long> frequencyOfList(List<String> falcons){
        if(falcons.isEmpty()){
            return new HashMap<>();
        }
        return falcons.stream().collect(Collectors.groupingBy(k->k, Collectors.counting()));
    }
    public static Map<Integer, Long> frequencyOfList(ArrayList<Integer> falcons){
        if(falcons.isEmpty()){
            return new HashMap<>();
        }
        return falcons.stream().collect(Collectors.groupingBy(k->k, Collectors.counting()));
    }


    public static String lowerFirst(String str) {
        char[] cs=str.toCharArray();
        // judge if the first letter is capital. If it is, convert it to its lowercase.
        if(cs[0]>=65&&cs[0]<=90)
            cs[0]+=32;
        return String.valueOf(cs);
    }

    public static String upperFirst(String str) {
//        System.out.println(str);
        char[] cs=str.toCharArray();
        // judge if the first letter is capital. If it is, convert it to its lowercase.
        if(cs[0]>=97&&cs[0]<=122)
            cs[0]-=32;
        return String.valueOf(cs);
    }


    public static int appearances(String target, String str) {
//this is the method for finding the occurrences for String
//this part is what I am asking help for
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==target.charAt(0))
                count++;
        }
        return count;

    }
    public static int appearances(int datum, ArrayList<Integer> nums) {
//this is the method for finding the occurrences for ArrayList
//this part is what I am asking help for
        int count = 0;
        for (int i = 0; i < nums.size(); i++) {
            if(nums.get(i)==datum)
                count++;
        }
        return count;

    }

    public static int appearances(String datum, ArrayList<String> nums) {
//this is the method for finding the occurrences for ArrayList
//this part is what I am asking help for
        int count = 0;
        for (int i = 0; i < nums.size(); i++) {
            if(nums.get(i).equals(datum))
                count++;
        }
        return count;

    }
//    public static String split(String methodName){
//        String splitTokens = "";
//        String camel = parseWithCamelCase(methodName);
//        if(camel!=null){
//            splitTokens = camel;
//        }
//        else{
//            String underscore = parseWithUnderScore(methodName);
//            if(underscore!=null)
//                splitTokens = underscore;
//            else
//                splitTokens = methodName;
//        }
//        return splitTokens;
//    }
//    public static String parseWithUnderScore(String methodName){
//        String[] subTokensArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(methodName,"_");
//        StringBuilder subTokens = new StringBuilder();
//        int length = subTokensArray.length;
//        for (int i = 0; i < length; i ++) {
//            String subToken = subTokensArray[i];
//            if (NumberUtils.isDigits(subToken)) {// remove numeric letter.
//                continue;
//            }
//            subTokens.append(subTokensArray[i].toLowerCase(Locale.ROOT)).append(",");
//        }
//        if (subTokens.length() == 0) {
////            System.err.println(methodName);
//            return null;
//        }
//        return subTokens.toString().substring(0, subTokens.length() - 1);
//    }
//    public static String parseWithCamelCase(String methodName) {
//        String[] subTokensArray = StringUtils.splitByCharacterTypeCamelCase(methodName);
//
//        StringBuilder subTokens = new StringBuilder();
//        int length = subTokensArray.length;
//        for (int i = 0; i < length; i ++) {
//            String subToken = subTokensArray[i];
//            if ("_".equals(subToken)) {// remove underscore.
//                continue;
//            } else if (NumberUtils.isDigits(subToken)) {// remove numeric letter.
//                continue;
//            }
//            subTokens.append(subTokensArray[i].toLowerCase(Locale.ROOT)).append(",");
//        }
//        if (subTokens.length() == 0) {
////            System.err.println(methodName);
//            return null;
//        }
//        return subTokens.toString().substring(0, subTokens.length() - 1);
//    }

    public static Boolean identifyCommonPartsOfTwoStrings(String generated_name, String ground_truth_name) throws FileNotFoundException {
        String result = maxSubstring(generated_name,ground_truth_name);
        if(result!=null&&result.length()>2){
//            System.out.println(result);
            return true;
        }
        return false;
    }
    public static String maxSubstring(String strOne, String strTwo){
        // 参数检查
        if(strOne==null || strTwo == null){
            return null;
        }
        if(strOne.equals("") || strTwo.equals("")){
            return null;
        }
        // 二者中较长的字符串
        String max = "";
        // 二者中较短的字符串
        String min = "";
        if(strOne.length() < strTwo.length()){
            max = strTwo;
            min = strOne;
        } else{
            max = strTwo;
            min = strOne;
        }
        String current = "";
        // 遍历较短的字符串，并依次减少短字符串的字符数量，判断长字符是否包含该子串
        for(int i=0; i<min.length(); i++){
            for(int begin=0, end=min.length()-i; end<=min.length(); begin++, end++){
                current = min.substring(begin, end);
                if(max.contains(current)){
                    return current;
                }
            }
        }
        return null;
    }

//    public static boolean ifExistCommonPartsBetweenVariableNamesAndRightPart(String toString, String ground_truth_name) {
//        String tokenizedVariable = split(ground_truth_name).toLowerCase(Locale.ROOT);
//        String tokenizedInitializer = split(toString).toLowerCase(Locale.ROOT);
//        String [] t1 = tokenizedInitializer.split(",");
//        String [] t2 = tokenizedVariable.split(",");
//        List<String> list1 = Arrays.asList(t1);
//        List<String> list2 = Arrays.asList(t2);
////        System.out.println(Arrays.toString(t1));
////        System.out.println(Arrays.toString(t2));
//        for(int i =0;i< list2.size();i++){
//            String s = list2.get(i);
//            if(!list1.contains(s)){
//                return false;
//            }
//        }
//        return true;
//    }
}
