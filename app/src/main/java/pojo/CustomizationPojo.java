package pojo;

import java.util.ArrayList;

public class CustomizationPojo {


    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {

        public int cart_item_id;
        public int item_id;
        public int quantity;
        public String item_name;
        public String item_image;
        public int image_enable;
        public int is_offered;
        public String category;
        public String paid_customization;
        public String free_customization;
        public String price;

    }
}
