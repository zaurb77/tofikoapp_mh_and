package pojo;

import java.util.ArrayList;

public class CategoryItemPojo {

    public String status;
    public String message;

    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData {
        public int id;
        public String name;
        public String image;
        public int image_enable;
        public String price;
        public int is_show;
        public String ingredients;
        public String allergens;
        public String in_cart;
        public String cart_item_id;
        public String quantity;
        public int is_favourite;
        public int qty;
        public int is_veg;
        public int is_spicy;
        public int is_taste;
        public String prev_customization;

        public ArrayList<FreeCustomization> remove_customization = new ArrayList<>();
        public ArrayList<PaidCustomization> customization = new ArrayList<>();
        public ArrayList<cookingGrades> cooking_grades = new ArrayList<>();
        public ArrayList<Taste> taste = new ArrayList<>();

        public ArrayList<CartItem> cart_items = new ArrayList<>();


        public class FreeCustomization {
            public int id;
            public String name;
            public boolean freeSelected;

        }

        public class PaidCustomization {
            public int id;
            public String name;
            public String price;
            public boolean paidSelected;
        }

        public class Taste {
            public int id;
            public String name;
            public String price;
            public int is_default;
        }

        public class cookingGrades {
            public int id;
            public String name;
            public String price;
            public int is_default;
        }

        public class CartItem {
            public int cart_item_id;
            public String quantity;

        }


    }
}


