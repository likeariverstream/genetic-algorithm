package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm<C extends Chromosome<C>> {
    public enum SelectionType {
        ROULETTE, TOURNAMENT;
    }

    private ArrayList<C> population;
    private double mutationChance;
    private double crossoverChance;
    private SelectionType selectionType;
    private Random random;

    public GeneticAlgorithm(List<C> initialPopulation, double mutationChance, double crossoverChance, SelectionType selectionType) {
        this.population = new ArrayList<>(initialPopulation);
        this.mutationChance = mutationChance;
        this.crossoverChance = crossoverChance;
        this.selectionType = selectionType;
        this.random = new Random();

    }

    private List<C> pickRoulette(double[] wheel, int numPicks) {
        List<C> picks = new ArrayList<>();
        for (int i = 0; i < numPicks; i++) {
            double pick = random.nextDouble();
            for (int j = 0; j < wheel.length; j++) {
                pick -= wheel[j];
                if (pick <= 0) { // Не работает при отрицательных значениях жизнеспособности
                    picks.add(population.get(j));
                    break;
                }
            }
        }
        return picks;
    }

    // Посредством турнирного отбора выбираем определенное число хромосом

    private List<C> pickTournament(int numParticipants, int numPicks) {
        // находим случайным образом numParticipants для участия в отборе
        Collections.shuffle(population);
        List<C> tournament = population.subList(0, numParticipants);
        // выбираем numPicks с наилучшей жизнеспособностью
        Collections.sort(tournament, Collections.reverseOrder());
        return tournament.subList(0, numPicks);
    }

    // Замена популяции новым поколением особей
    private void reproduceAndReplace() {
        ArrayList<C> nextPopulation = new ArrayList<>();
        // продолжаем, пока не заполним особями все новое поколение:
        while (nextPopulation.size() < population.size()) {
            // выбор двух родителей
            List<C> parents;
            if (selectionType == SelectionType.ROULETTE) {
                // создание метода рулетки
                double totalFitness = population.stream().mapToDouble(C::fitness).sum();
                double[] wheel = population.stream().mapToDouble(C -> C.fitness() / totalFitness).toArray();
                parents = pickRoulette(wheel, 2);
            } else { // турнирный отбор
                parents = pickTournament(population.size() / 2, 2);
            }
            // потенциальное скрещивание двух родителей
            if (random.nextDouble() < crossoverChance) {
                C parent1 = parents.get(0);
                C parent2 = parents.get(1);
                nextPopulation.addAll(parent1.crossover(parent2));
            } else { // добавление двух родителей
                nextPopulation.addAll(parents);
            }
        }
        if (nextPopulation.size() > population.size()) {
            nextPopulation.remove(0);
        }

        // заменяем ссылку поколение
        population = nextPopulation;
    }

    // каждая особь мутирует с вероятностью mutationChance
    private void mutate() {
        for (C individual : population) {
            if (random.nextDouble() < mutationChance) {
                individual.mutate();
            }
        }
    }

    // выполнение генетического алгоритма для maxGenerations итераций и возвращение лучшей из найденных особей
    public C run(int maxGenerations, double threshold) {
        C best = Collections.max(population).copy();
        for (int generation = 0; generation < maxGenerations; generation++) {
            // досрочный выбор при достижении порога
            if (best.fitness() >= threshold) {
                return best;
            }
            // печать результатов отладки
            System.out.println("Generation " + generation + " Best " + best
                    .fitness() + " Avg " + population
                    .stream().mapToDouble(C::fitness).average().orElse(0.0));
            reproduceAndReplace();
            mutate();
            C highest = Collections.max(population);
            if (highest.fitness() > best.fitness()) {
                best = highest.copy();
            }
        }
        return best;
    }

}
