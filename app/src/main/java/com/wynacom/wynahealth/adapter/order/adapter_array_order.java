package com.wynacom.wynahealth.adapter.order;

public class adapter_array_order {
    String order_id               = null;

    public adapter_array_order(String id){
        super();
        this.order_id             = id;
    }

    public String getOrder_id() {
        return order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
