package com.sms.model;

public class CustomerAddress {

    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;

    public CustomerAddress() {
    }

    public CustomerAddress(String street, String city, String state, String zip, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String s) {
        this.street = s;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String c) {
        this.city = c;
    }

    public String getState() {
        return state;
    }

    public void setState(String s) {
        this.state = s;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String z) {
        this.zip = z;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String c) {
        this.country = c;
    }

    /**
     * NOTE: toPostgresLiteral() is NO LONGER USED for insert/update. We now use
     * ROW(?,?,?,?,?) in SQL instead. Kept only for backward compatibility if
     * anything else calls it.
     */
    public String toPostgresLiteral() {
        return "(" + esc(street) + "," + esc(city) + "," + esc(state)
                + "," + esc(zip) + "," + esc(country) + ")";
    }

    private String esc(String v) {
        if (v == null || v.isEmpty()) {
            return "";
        }
        if (v.contains(",") || v.contains("(") || v.contains(")")
                || v.contains("\"") || v.contains("\\") || v.contains(" ")) {
            return "\"" + v.replace("\\", "\\\\").replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /**
     * Parse from PostgreSQL composite string: (street,city,state,zip,country)
     * Properly handles quoted fields with commas and escaped quotes.
     */
    public static CustomerAddress fromPostgresLiteral(String literal) {
        if (literal == null || literal.isEmpty()) {
            return new CustomerAddress();
        }

        String inner = literal.trim();
        if (inner.startsWith("(")) {
            inner = inner.substring(1);
        }
        if (inner.endsWith(")")) {
            inner = inner.substring(0, inner.length() - 1);
        }

        // ═══ Proper composite literal parser ═══
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int i = 0;

        while (i < inner.length()) {
            char ch = inner.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    // Check for escaped quote ""
                    if (i + 1 < inner.length() && inner.charAt(i + 1) == '"') {
                        current.append('"');
                        i += 2;
                        continue;
                    } else {
                        // End of quoted field
                        inQuotes = false;
                        i++;
                        continue;
                    }
                } else if (ch == '\\' && i + 1 < inner.length()) {
                    current.append(inner.charAt(i + 1));
                    i += 2;
                    continue;
                } else {
                    current.append(ch);
                    i++;
                    continue;
                }
            } else {
                if (ch == '"') {
                    inQuotes = true;
                    i++;
                    continue;
                } else if (ch == ',') {
                    fields.add(current.toString());
                    current = new StringBuilder();
                    i++;
                    continue;
                } else {
                    current.append(ch);
                    i++;
                    continue;
                }
            }
        }
        // Don't forget the last field
        fields.add(current.toString());

        CustomerAddress a = new CustomerAddress();
        if (fields.size() >= 1) {
            a.setStreet(fields.get(0).trim());
        }
        if (fields.size() >= 2) {
            a.setCity(fields.get(1).trim());
        }
        if (fields.size() >= 3) {
            a.setState(fields.get(2).trim());
        }
        if (fields.size() >= 4) {
            a.setZip(fields.get(3).trim());
        }
        if (fields.size() >= 5) {
            a.setCountry(fields.get(4).trim());
        }
        return a;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (street != null && !street.isEmpty()) {
            sb.append(street);
            first = false;
        }
        if (city != null && !city.isEmpty()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(city);
            first = false;
        }
        if (state != null && !state.isEmpty()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(state);
            first = false;
        }
        if (zip != null && !zip.isEmpty()) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(zip);
            first = false;
        }
        if (country != null && !country.isEmpty()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(country);
        }
        return sb.toString();
    }
}
