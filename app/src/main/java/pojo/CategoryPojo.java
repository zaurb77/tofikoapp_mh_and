package pojo;

import java.util.ArrayList;

public class CategoryPojo {

    public int status;
    public String message;
    public String res_open_error;
    public String pre_order_accept;

    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public int id;
        public String name;
        public String image;
        public boolean selectedItem = false;
    }
}


