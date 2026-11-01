package md.ecosort.ecosort.model;

// Ultima stare cunoscută a unui coș cu senzor. Câmpurile null: încă nu a sosit nicio măsurătoare.
public record StareCos(int punctId, String dispozitiv, int canal, double capacitateKg,
                       Integer greutateG, Integer distantaMm, Integer umplerePct, Long vechimeSecunde) {

    public static final long PRAG_OFFLINE_S = 90;   // 18 mesaje pierdute la un mesaj la 5 s

    public boolean arePrimitDate() { return vechimeSecunde != null; }

    public boolean esteOnline() { return vechimeSecunde != null && vechimeSecunde <= PRAG_OFFLINE_S; }
}