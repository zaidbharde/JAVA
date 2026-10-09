/** Formats byte arrays with offsets, hexadecimal bytes, and printable text. */
public final class HexDumpFormatter {
    private HexDumpFormatter() { }

    public static String format(byte[] data, int width) {
        if (data == null) throw new NullPointerException("data");
        if (width < 1 || width > 32) throw new IllegalArgumentException("width must be one to 32");
        StringBuilder output = new StringBuilder();
        for (int offset = 0; offset < data.length; offset += width) {
            int end = Math.min(offset + width, data.length);
            output.append(String.format("%08x  ", offset));
            for (int i = offset; i < offset + width; i++) {
                if (i < end) output.append(String.format("%02x ", data[i] & 0xff));
                else output.append("   ");
                if (i == offset + width / 2 - 1) output.append(' ');
            }
            output.append(" | ");
            for (int i = offset; i < end; i++) {
                int value = data[i] & 0xff;
                output.append(value >= 32 && value < 127 ? (char) value : '.');
            }
            output.append('\n');
        }
        return output.toString();
    }
}
