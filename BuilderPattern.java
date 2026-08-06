public class BuilderPattern {
    static class Pizza {
        private final String size;
        private final boolean cheese;
        private final boolean pepperoni;
        private final boolean mushrooms;

        private Pizza(Builder builder) {
            this.size = builder.size;
            this.cheese = builder.cheese;
            this.pepperoni = builder.pepperoni;
            this.mushrooms = builder.mushrooms;
        }

        public String toString() {
            return "Pizza[size=" + size + ", cheese=" + cheese + ", pepperoni=" + pepperoni + ", mushrooms=" + mushrooms + "]";
        }

        static class Builder {
            private String size = "medium";
            private boolean cheese, pepperoni, mushrooms;

            public Builder size(String size) { this.size = size; return this; }
            public Builder cheese() { this.cheese = true; return this; }
            public Builder pepperoni() { this.pepperoni = true; return this; }
            public Builder mushrooms() { this.mushrooms = true; return this; }
            public Pizza build() { return new Pizza(this); }
        }
    }

    public static void main(String[] args) {
        Pizza pizza = new Pizza.Builder()
                .size("large")
                .cheese()
                .pepperoni()
                .build();
        System.out.println(pizza);
    }
}
