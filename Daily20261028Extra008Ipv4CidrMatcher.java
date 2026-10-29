public final class Daily20261028Extra008Ipv4CidrMatcher {
    private final long network;
    private final long mask;

    private Daily20261028Extra008Ipv4CidrMatcher(String cidr) {
        if (cidr == null) throw new IllegalArgumentException("CIDR must not be null");
        int slash = cidr.indexOf('/');
        if (slash <= 0 || slash != cidr.lastIndexOf('/') || slash == cidr.length() - 1)
            throw new IllegalArgumentException("Expected address/prefix: " + cidr);
        long address = parseAddress(cidr.substring(0, slash));
        int prefix;
        try {
            String text = cidr.substring(slash + 1);
            if (!text.matches("[0-9]{1,2}")) throw new NumberFormatException();
            prefix = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid prefix length", ex);
        }
        if (prefix < 0 || prefix > 32) throw new IllegalArgumentException("Prefix must be 0..32");
        mask = prefix == 0 ? 0 : (0xffff_ffffL << (32 - prefix)) & 0xffff_ffffL;
        network = address & mask;
    }

    public static Daily20261028Extra008Ipv4CidrMatcher parse(String cidr) {
        return new Daily20261028Extra008Ipv4CidrMatcher(cidr);
    }

    public boolean contains(String address) {
        return (parseAddress(address) & mask) == network;
    }

    private static long parseAddress(String text) {
        if (text == null) throw new IllegalArgumentException("Address must not be null");
        String[] parts = text.split("\\.", -1);
        if (parts.length != 4) throw new IllegalArgumentException("Expected four IPv4 octets: " + text);
        long value = 0;
        for (String part : parts) {
            if (!part.matches("[0-9]{1,3}")) throw new IllegalArgumentException("Invalid IPv4 octet: " + part);
            int octet = Integer.parseInt(part);
            if (octet > 255) throw new IllegalArgumentException("IPv4 octet out of range: " + part);
            value = (value << 8) | octet;
        }
        return value;
    }

    public static void main(String[] args) {
        Daily20261028Extra008Ipv4CidrMatcher subnet = parse("192.168.1.17/24");
        if (!subnet.contains("192.168.1.255") || subnet.contains("192.168.2.0")
                || !parse("0.0.0.0/0").contains("203.0.113.9")
                || !parse("10.0.0.1/32").contains("10.0.0.1"))
            throw new AssertionError("CIDR membership self-check failed");
        try {
            parse("192.168.1.1/33");
            throw new AssertionError("Invalid prefix accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("Ipv4CidrMatcher self-check passed");
    }
}
