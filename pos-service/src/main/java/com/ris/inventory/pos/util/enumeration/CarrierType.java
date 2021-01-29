package com.ris.inventory.pos.util.enumeration;

public enum CarrierType {
    POST_OFFICE("POST OFFICE"),
    FED_EX("FED-EX"),
    UPS("UPS"),
    DHL("DHL"),
    OWN_CARRIER("OWN CARRIER"),
    ERROR("Error");

    private String carrier;

    CarrierType(String carrier) {
        this.carrier = carrier;
    }

    public static CarrierType from(String carrier) {
        if (carrier.equalsIgnoreCase("post_office") || carrier.equalsIgnoreCase("post office"))
            return CarrierType.POST_OFFICE;
        if (carrier.equalsIgnoreCase("FED_EX"))
            return CarrierType.FED_EX;
        if (carrier.equalsIgnoreCase("UPS"))
            return CarrierType.UPS;
        if (carrier.equalsIgnoreCase("DHL"))
            return CarrierType.DHL;
        if (carrier.equalsIgnoreCase("OWN_CARRIER"))
            return CarrierType.OWN_CARRIER;
        else
            return CarrierType.ERROR;
    }

    public String getCarrier() {
        return carrier;
    }
}
