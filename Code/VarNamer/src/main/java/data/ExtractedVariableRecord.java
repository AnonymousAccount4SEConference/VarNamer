package data;

public class ExtractedVariableRecord {
    private String type;
    private String initializer;
    private String receiver;
    private String splitted_receiver;
    private String method_name;
    private String splitted_method_name;
    private int argument_num;
    private String [] arguments;
    private String ground_truth;
    private String splitted_ground_truth;
    private String splitted_selected_expression;

    public ExtractedVariableRecord(String type, String initializer,String receiver,String splitted_receiver,String method_name,String splitted_method_name,int argument_num,String [] arguments,String ground_truth
            ,String splitted_ground_truth,String splitted_selected_expression){
        this.type = type;
        this.argument_num = argument_num;
        this.arguments = arguments;
        this.method_name = method_name;
        this.receiver = receiver;
        this.initializer = initializer;
        this.splitted_receiver = splitted_receiver;
        this.splitted_method_name = splitted_method_name;
        this.ground_truth = ground_truth;
        this.splitted_ground_truth = splitted_ground_truth;
        this.splitted_selected_expression = splitted_selected_expression;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getInitializer() {
        return initializer;
    }

    public void setInitializer(String initializer) {
        this.initializer = initializer;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSplitted_receiver() {
        return splitted_receiver;
    }

    public void setSplitted_receiver(String splitted_receiver) {
        this.splitted_receiver = splitted_receiver;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String getSplitted_method_name() {
        return splitted_method_name;
    }

    public void setSplitted_method_name(String splitted_method_name) {
        this.splitted_method_name = splitted_method_name;
    }

    public int getArgument_num() {
        return argument_num;
    }

    public void setArgument_num(int argument_num) {
        this.argument_num = argument_num;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getGround_truth() {
        return ground_truth;
    }

    public void setGround_truth(String ground_truth) {
        this.ground_truth = ground_truth;
    }

    public String getSplitted_ground_truth() {
        return splitted_ground_truth;
    }

    public void setSplitted_ground_truth(String splitted_ground_truth) {
        this.splitted_ground_truth = splitted_ground_truth;
    }

    public String getSplitted_selected_expression() {
        return splitted_selected_expression;
    }

    public void setSplitted_selected_expression(String splitted_selected_expression) {
        this.splitted_selected_expression = splitted_selected_expression;
    }
}
