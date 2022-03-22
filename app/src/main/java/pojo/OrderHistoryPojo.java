package pojo;

import java.util.ArrayList;

public class OrderHistoryPojo {

    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public int restaurant_id;
        public String restaurant_name;
        public String image;
        public String transaction_id;
        public String order_date;
        public String cart_id;
        public String order_amount;
        public String order_number;
        public String rating;
        public String customer_comment;
        public String restaurant_comment;
        public String cart_item_status;
        public String order_status;
    }
}
