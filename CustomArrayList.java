import java.util.Arrays;

public class CustomArrayList<T> {
    private Object[] data;
    private int size = 0;

    public CustomArrayList() {
        data = new Object[4];
    }

    public void add(T item) {
        if (size == data.length) resize();
        data[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (T) data[index];
    }

    public void remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        size--;
    }

    private void resize() {
        data = Arrays.copyOf(data, data.length * 2);
    }

    public int size() { return size; }

    public static void main(String[] args) {
        CustomArrayList<String> list = new CustomArrayList<>();
        list.add("a"); list.add("b"); list.add("c"); list.add("d"); list.add("e");
        for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
        list.remove(1);
        System.out.println("After remove index 1:");
        for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
    }
}
