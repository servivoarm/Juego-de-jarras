import java.util.*;

public class Main {
    static final int jug_4 = 4;
    static final int jug_3 = 3;
    static final state OBJETIVE = new state(2,0);

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        //reglas del juego

        List<Accion> reglas = List.of(
                new Accion(1, "Llenar la jarra de 4 litros", e -> new state (jug_4,e.b)),
                new Accion(2, "LLenar la jarra de 3 litros", e -> new state (e.a,jug_3)),
                new Accion(3, "Vaciar la jarra de 4 litros", e -> new state(0, e.b)),
                new Accion(4, "Vaciar la jarra de 3 litros", e -> new state(e.a, 0)),
                new Accion(5, "Verter de la jarra de 3 litros a la de 4 litros hasta que la de 4 esté llena", e -> {
                    int transfer = Math.min(e.b, jug_4 - e.a);
                    return new state(e.a + transfer, e.b - transfer);
                }),
                new Accion(6, "Verter de la jarra de 4 litros a la de 3 litros hasta que la de 3 esté llena", e -> {
                    int transfer = Math.min(e.a, jug_3 - e.b);
                    return new state(e.a - transfer, e.b + transfer);
                }),
                new Accion(7, "Verter todo el agua de la jarra de 3 litros a la de 4 litros", e -> {
                    if (e.a + e.b <= jug_4)
                        return new state(e.a + e.b, 0);
                    else return e; // no válida
                }),
                new Accion(8, "Verter todo el agua de la jarra de 4 litros a la de 3 litros", e -> {
                    if (e.a + e.b <= jug_3)
                        return new state(0, e.a + e.b);
                    else return e; // no válida
                })
        );

        System.out.println("===== JUEGO DE LAS JARRAS =====");
        System.out.println("Objetivo: obtener exactamente 2 litros en la jarra de A (de 4 litros)");
        System.out.println("Capacidad: Jarra A = 4L | Jarra B = 3 L");
        System.out.println("Reglas disponibles: ");
        for (Accion r : reglas){
            System.out.printf("Regla %d: %s\n", r.numero , r.descripcion);
        }

        String jugar;

        do{
            state actual = new state(0,0);
            List<String> reglasUsadas = new ArrayList<>();
            int costo = 0;

            while (!actual.equals(OBJETIVE)){
                System.out.println("\n Estado actual: " + actual);

                List<Accion> posibles = new ArrayList<>();
                for(Accion r : reglas){
                    state nuevo = r.aplicar(actual);
                    if (!nuevo.equals(actual)){
                        posibles.add(r);
                    }
                }

                System.out.println("Reglas permitidas: ");
                for(Accion r : posibles){
                    System.out.printf("Regla %d: %s\n", r.numero, r.descripcion);
                }

                int eleccion = -1;
                while(true){
                    System.out.print("Elige el número de la regla que desea aplicar: ");
                    try{
                        eleccion = Integer.parseInt(sc.nextLine());
                        final int elegido = eleccion;
                        if (posibles.stream().anyMatch(r -> r.numero == elegido)) break;
                        else System.out.println("\nEsa regla no es valida en este paso.");
                    } catch (Exception e) {
                        System.out.println("\nEntrada invalida. Intenta con un número.");
                    }
                }

                Accion seleccionada = reglas.get(eleccion -1);
                actual = seleccionada.aplicar(actual);
                reglasUsadas.add("Regla " + seleccionada.numero + ": "+ seleccionada.descripcion);
                costo++;
            }
            System.out.println("\n¡Objetivo alcanzado!");
            System.out.println("\n Estado final: " + actual);
            for(String r : reglasUsadas){
                System.out.println("- " + r);
            }
            System.out.println("\nEl costo total: " + costo);

            System.out.println("\nDesea volver a jugar? (s/n): ");
            jugar = sc.nextLine().toLowerCase();
        }while(jugar.equals("s"));

    }

    static class state {
        int a , b;

        state(int a, int b){
            this.a = a;
            this.b = b;
        }

        public boolean equals(Object o) {
            if (!(o instanceof state e)) return false;
            return a == e.a && b == e.b;
        }

        public String toString() {
            return String.format("Jarra A: %dL, Jarra B: %dL", a, b);
        }
    }

    static class Accion {
        int numero;
        String descripcion;
        Transicion transicion;

        Accion(int numero, String descripcion, Transicion t){
            this.numero = numero;
            this.descripcion = descripcion;
            transicion = t;
        }
        state aplicar(state e){
            return transicion.aplicar(e);
        }
    }

    interface Transicion {
        state aplicar(state e);
    }
}