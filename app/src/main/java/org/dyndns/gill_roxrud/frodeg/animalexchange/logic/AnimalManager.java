package org.dyndns.gill_roxrud.frodeg.animalexchange.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.SparseArray;

import org.dyndns.gill_roxrud.frodeg.animalexchange.AnimalExchangeApplication;
import org.dyndns.gill_roxrud.frodeg.animalexchange.GameState;
import org.dyndns.gill_roxrud.frodeg.animalexchange.InvalidPositionException;
import org.dyndns.gill_roxrud.frodeg.animalexchange.Point;
import org.dyndns.gill_roxrud.frodeg.animalexchange.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;

/*
 https://www.pexels.com/
 https://www.goodfreephotos.com/
*/

public class AnimalManager {

    private static AnimalManager instance = null;

    private final SparseArray<AnimalGroup> animalGroupMap = new SparseArray<>();
    private final ArrayList<AnimalGroup> animalGroupArray = new ArrayList<>();
    private final SparseArray<Animal> animalMap = new SparseArray<>();
    private final ArrayList<Animal> animalArray = new ArrayList<>();

    private Bitmap cachedHiddenAnimalGiftBitmap = null;
    private int cachedHiddenAnimalGiftBitmapSize = -1;

    private final Point<Double> previousAcceptedPos = new Point<>(AnimalExchangeApplication.EAST+1.0, AnimalExchangeApplication.NORTH+1.0);
    private long previousAcceptedPosTime = 0L;

    public class MovementInfo {
        public double food;
        public double speed;
    }


    public AnimalManager() {
        initializeAnimalGroups();
        initializeAnimals();
    }

    private void initializeAnimalGroups() {
        addGroup(0, R.string.domesticanimals_bronze);
        addGroup(1, R.string.domesticanimals_silver);
        addGroup(2, R.string.domesticanimals_gold);
        addGroup(3, R.string.seaanimals_silver);
        addGroup(4, R.string.seaanimals_gold);
        addGroup(5, R.string.domesticbirds_silver);
        addGroup(6, R.string.domesticbirds_gold);
        addGroup(7, R.string.wildanimals_bronze);
        addGroup(8, R.string.wildanimals_silver);
        addGroup(9, R.string.wildanimals_gold);
        addGroup(10, R.string.exoticbirds_silver);
        addGroup(11, R.string.exoticbirds_gold);
        addGroup(12, R.string.exoticanimals_bronze);
        addGroup(13, R.string.exoticanimals_silver);
        addGroup(14, R.string.exoticanimals_gold);
    }

    private void initializeAnimals() {
        addAnimal(0, R.string.cat, R.drawable.square_cat, R.drawable.rounded_cat,
                  R.string.domesticanimals_bronze, 0L, 827834137L, 250);
        addAnimal( 1, R.string.dog, R.drawable.square_dog, R.drawable.rounded_dog,
                  R.string.domesticanimals_bronze, 827834138L,1655668274L, 252);
        addAnimal( 2, R.string.guinea_pig, R.drawable.square_guinea_pig, R.drawable.rounded_guinea_pig,
                  R.string.domesticanimals_silver, 1655668275L,2483502412L, 257);
        addAnimal( 3, R.string.rabbit, R.drawable.square_rabbit, R.drawable.rounded_rabbit,
                  R.string.domesticanimals_bronze, 2483502413L,3311336549L, 267);
        addAnimal( 4, R.string.donkey, R.drawable.square_donkey, R.drawable.rounded_donkey,
                  R.string.domesticanimals_silver, 3311336550L,3614511133L, 280);
        addAnimal( 5, R.string.hamster, R.drawable.square_hamster, R.drawable.rounded_hamster,
                  R.string.domesticanimals_bronze, 3614511134L,3824241275L, 297);
        addAnimal( 6, R.string.pig, R.drawable.square_pig, R.drawable.rounded_pig,
                  R.string.domesticanimals_gold, 3824241276L,3969328412L, 317);
        addAnimal( 7, R.string.sheep, R.drawable.square_sheep, R.drawable.rounded_sheep,
                  R.string.domesticanimals_silver, 3969328413L,4069696805L, 341);
        addAnimal( 8, R.string.horse, R.drawable.square_horse, R.drawable.rounded_horse,
                  R.string.domesticanimals_gold, 4069696806L,4139129661L, 369);
        addAnimal( 9, R.string.cow, R.drawable.square_cow, R.drawable.rounded_cow,
                  R.string.domesticanimals_silver, 4139129662L,4187161928L, 401);
        addAnimal(10, R.string.shrimp, R.drawable.square_shrimp, R.drawable.rounded_shrimp,
                  R.string.seaanimals_silver, 4187161929L,4220389694L, 436);
        addAnimal(11, R.string.goat, R.drawable.square_goat, R.drawable.rounded_goat,
                  R.string.domesticanimals_gold, 4220389695L,4243376001L, 474);
        addAnimal(12, R.string.crab, R.drawable.square_crab, R.drawable.rounded_crab,
                  R.string.seaanimals_silver, 4243376002L,4259277471L, 517);
        addAnimal(13, R.string.salmon, R.drawable.square_salmon, R.drawable.rounded_salmon,
                  R.string.seaanimals_gold, 4259277472L,4270277791L, 562);
        addAnimal(14, R.string.tuna, R.drawable.square_tuna, R.drawable.rounded_tuna,
                  R.string.seaanimals_silver, 4270277792L,4277887593L, 612);
        addAnimal(15, R.string.lobster, R.drawable.square_lobster, R.drawable.rounded_lobster,
                  R.string.seaanimals_gold, 4277887594L,4283151903L, 664);
        addAnimal(16, R.string.hermitcrab, R.drawable.square_hermitcrab, R.drawable.rounded_hermitcrab,
                  R.string.seaanimals_silver, 4283151904L,4286793647L, 720);
        addAnimal(17, R.string.hen, R.drawable.square_hen, R.drawable.rounded_hen,
                  R.string.domesticbirds_silver, 4286793648L,4289312933L, 780);
        addAnimal(18, R.string.halibut, R.drawable.square_halibut, R.drawable.rounded_halibut,
                  R.string.seaanimals_gold, 4289312934L,4291055725L, 843);
        addAnimal(19, R.string.turkey, R.drawable.square_turkey, R.drawable.rounded_turkey,
                  R.string.domesticbirds_gold, 4291055726L,4292261354L, 909);
        addAnimal(20, R.string.dove, R.drawable.square_dove, R.drawable.rounded_dove,
                  R.string.domesticbirds_silver, 4292261355L,4293095384L, 978);
        addAnimal(21, R.string.duck, R.drawable.square_duck, R.drawable.rounded_duck,
                  R.string.domesticbirds_gold, 4293095385L,4293672349L,1051);
        addAnimal(22, R.string.goose, R.drawable.square_goose, R.drawable.rounded_goose,
                  R.string.domesticbirds_silver, 4293672350L,4294071482L,1126);
        addAnimal(23, R.string.squirrel, R.drawable.square_squirrel, R.drawable.rounded_squirrel,
                  R.string.wildanimals_bronze, 4294071483L,4294347594L,1205);
        addAnimal(24, R.string.guineafowl, R.drawable.square_guineafowl, R.drawable.rounded_guineafowl,
                  R.string.domesticbirds_gold, 4294347595L,4294538603L,1286);
        addAnimal(25, R.string.hare, R.drawable.square_hare, R.drawable.rounded_hare,
                  R.string.wildanimals_bronze, 4294538604L,4294670739L,1371);
        addAnimal(26, R.string.peacock, R.drawable.square_peacock, R.drawable.rounded_peacock,
                  R.string.domesticbirds_gold, 4294670740L,4294762148L,1458);
        addAnimal(27, R.string.fox, R.drawable.square_fox, R.drawable.rounded_fox,
                  R.string.wildanimals_silver, 4294762149L,4294825383L,1548);
        addAnimal(28, R.string.hedgehog, R.drawable.square_hedgehog, R.drawable.rounded_hedgehog,
                  R.string.wildanimals_bronze, 4294825384L,4294869127L,1641);
        addAnimal(29, R.string.stoat, R.drawable.square_stoat, R.drawable.rounded_stoat,
                  R.string.wildanimals_silver, 4294869128L,4294899388L,1737);
        addAnimal(30, R.string.moose, R.drawable.square_moose, R.drawable.rounded_moose,
                  R.string.wildanimals_bronze, 4294899389L,4294920322L,1835);
        addAnimal(31, R.string.bear, R.drawable.square_bear, R.drawable.rounded_bear,
                  R.string.wildanimals_gold, 4294920323L,4294934804L,1935);
        addAnimal(32, R.string.deer, R.drawable.square_deer, R.drawable.rounded_deer,
                  R.string.wildanimals_silver, 4294934805L,4294944822L,2038);
        addAnimal(33, R.string.lynx, R.drawable.square_lynx, R.drawable.rounded_lynx,
                  R.string.wildanimals_gold, 4294944823L,4294951752L,2144);
        addAnimal(34, R.string.boar, R.drawable.square_boar, R.drawable.rounded_boar,
                  R.string.wildanimals_silver, 4294951753L,4294956546L,2251);
        addAnimal(35, R.string.parakeet, R.drawable.square_parakeet, R.drawable.rounded_parakeet,
                  R.string.exoticbirds_silver, 4294956547L,4294959862L,2361);
        addAnimal(36, R.string.wolf, R.drawable.square_wolf, R.drawable.rounded_wolf,
                  R.string.wildanimals_gold, 4294959863L,4294962156L,2473);
        addAnimal(37, R.string.eagle, R.drawable.square_eagle, R.drawable.rounded_eagle,
                  R.string.wildanimals_gold, 4294962157L,4294963743L,2587);
        addAnimal(38, R.string.ostrich, R.drawable.square_ostrich, R.drawable.rounded_ostrich,
                  R.string.exoticbirds_silver, 4294963744L,4294964841L,2702);
        addAnimal(39, R.string.polarbear, R.drawable.square_polarbear, R.drawable.rounded_polarbear,
                  R.string.wildanimals_gold, 4294964842L,4294965600L,2820);
        addAnimal(40, R.string.falcon, R.drawable.square_falcon, R.drawable.rounded_falcon,
                  R.string.exoticbirds_gold, 4294965601L,4294966125L,2939);
        addAnimal(41, R.string.owl, R.drawable.square_owl, R.drawable.rounded_owl,
                  R.string.exoticbirds_silver, 4294966126L,4294966488L,3060);
        addAnimal(42, R.string.parrot, R.drawable.square_parrot, R.drawable.rounded_parrot,
                  R.string.exoticbirds_gold, 4294966489L,4294966739L,3182);
        addAnimal(43, R.string.camel, R.drawable.square_camel, R.drawable.rounded_camel,
                  R.string.exoticanimals_bronze, 4294966740L,4294966912L,3306);
        addAnimal(44, R.string.kingfisher, R.drawable.square_kingfisher, R.drawable.rounded_kingfisher,
                  R.string.exoticbirds_gold, 4294966913L,4294967032L,3431);
        addAnimal(45, R.string.hippo, R.drawable.square_hippo, R.drawable.rounded_hippo,
                  R.string.exoticanimals_bronze, 4294967033L,4294967115L,3558);
        addAnimal(46, R.string.eagle_owl, R.drawable.square_eagle_owl, R.drawable.rounded_eagle_owl,
                  R.string.exoticbirds_gold, 4294967116L,4294967172L,3685);
        addAnimal(47, R.string.lion, R.drawable.square_lion, R.drawable.rounded_lion,
                  R.string.exoticanimals_silver, 4294967173L,4294967211L,3814);
        addAnimal(48, R.string.zebra, R.drawable.square_zebra, R.drawable.rounded_zebra,
                  R.string.exoticanimals_bronze, 4294967212L,4294967238L,3943);
        addAnimal(49, R.string.giraffe, R.drawable.square_giraffe, R.drawable.rounded_giraffe,
                  R.string.exoticanimals_silver, 4294967239L,4294967257L,4073);
        addAnimal(50, R.string.elephant, R.drawable.square_elephant, R.drawable.rounded_elephant,
                  R.string.exoticanimals_bronze,4294967258L,4294967270L,4204);
        addAnimal(51, R.string.leopard, R.drawable.square_leopard, R.drawable.rounded_leopard,
                  R.string.exoticanimals_gold, 4294967271L,4294967279L,4336);
        addAnimal(52, R.string.kangaroo, R.drawable.square_kangaroo, R.drawable.rounded_kangaroo,
                  R.string.exoticanimals_silver, 4294967280L,4294967285L,4468);
        addAnimal(53, R.string.rhino, R.drawable.square_rhino, R.drawable.rounded_rhino,
                  R.string.exoticanimals_gold, 4294967286L,4294967289L,4601);
        addAnimal(54, R.string.tiger, R.drawable.square_tiger, R.drawable.rounded_tiger,
                  R.string.exoticanimals_silver, 4294967290L,4294967292L,4734);
        addAnimal(55, R.string.panda, R.drawable.square_panda, R.drawable.rounded_panda,
                  R.string.exoticanimals_gold, 4294967293L,4294967294L,4867);
        addAnimal(56, R.string.red_panda, R.drawable.square_red_panda, R.drawable.rounded_red_panda,
                  R.string.exoticanimals_gold, 4294967295L,4294967295L,5000);
    }

    private void addGroup(final int level, final int id) {
        AnimalGroup animalGroup = new AnimalGroup(level, id);
        animalGroupMap.put(id, animalGroup);
        animalGroupArray.add(level, animalGroup);
    }

    private void addAnimal(final int level, final int id, final int square_id, final int rounded_id, final int group_id,
                           final long distributionFrom, final long distributionTo, final int food) {
        Animal animal = new Animal(level, id, square_id, rounded_id, group_id, distributionFrom, distributionTo, food);
        animalMap.put(id, animal);
        animalArray.add(level, animal);
        getAnimalGroup(group_id).addAnimal(id);
    }

    AnimalGroup getAnimalGroup(final int id) {
        return animalGroupMap.get(id);
    }

    AnimalGroup getAnimalGroupByLevel(final int level) {
        return animalGroupArray.get(level);
    }

    Animal getAnimal(final int id) {
        return animalMap.get(id);
    }

    Animal getAnimalByLevel(final int level) {
        return animalArray.get(level);
    }

    public Animal getAnimalFromDistributionValue(final long value) {
        for (Animal animal : animalArray) {
            if (animal.containsDistributionValue(value)) {
                return animal;
            }
        }
        return null;
    }

    public Bitmap getHiddenAnimalGiftBitmap(final Context ctx, final int size) {
        if (cachedHiddenAnimalGiftBitmapSize != size) {
            if ((cachedHiddenAnimalGiftBitmap = AnimalManager.getBitmap(ctx, R.drawable.unknown, size)) !=null) {
                cachedHiddenAnimalGiftBitmapSize = size;
            }
        }
        return cachedHiddenAnimalGiftBitmap;
    }

    static Bitmap getBitmap(final Context ctx, final int drawable_id, final int size) {
        final Bitmap originalBitmap = BitmapFactory.decodeResource(ctx.getResources(), drawable_id);
        int originalSize = originalBitmap.getWidth();
        float scaleSize = ((float)size)/originalSize;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleSize, scaleSize);

        return Bitmap.createBitmap(originalBitmap, 0, 0, originalSize, originalSize, matrix, true);
    }

    public MovementInfo requestFoodT(final Point<Double> pos) {
        MovementInfo movementInfo = new MovementInfo();

        long now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        long timespanMillis = (now-previousAcceptedPosTime);
        if ((4L*AnimalExchangeApplication.LOCATION_UPDATE_INTERVAL*1000L) < timespanMillis) { //Allow some, but not a lot, of lost GPS connection
            previousAcceptedPosTime = now;
            previousAcceptedPos.set(pos.getX(), pos.getY());
            movementInfo.food = movementInfo.speed = 0.0;
            return movementInfo;
        }

        movementInfo.food = CalculateDistance(pos, previousAcceptedPos);
        if (AnimalExchangeApplication.LOCATION_UPDATE_DISTANCE > movementInfo.food ||
            0L == timespanMillis) {
            movementInfo.food = movementInfo.speed = 0.0;
            return movementInfo;
        }

        movementInfo.speed = movementInfo.food/(timespanMillis/3600.0); // km/h
        if (AnimalExchangeApplication.MAX_ALLOWED_SPEED >= movementInfo.speed) {
            if (!GameState.getInstance().getDB().PersistFoodT(movementInfo.food)) {
                movementInfo.food = 0.0;
            }
        }

        previousAcceptedPosTime = now;
        previousAcceptedPos.set(pos.getX(), pos.getY());
        return movementInfo;
    }

    Point<Double> calculateAnimalOffset(final int day) {
        MessageDigest messageDigest = createMessageDigest();
        if (messageDigest == null) {
            return new Point<>(0.0, 0.0);
        }

        try {
            messageDigest.update(Integer.toString(day).getBytes("utf-8"));
        } catch (Exception e) {
            return new Point<>(0.0, 0.0);
        }

        long hash = getHash(messageDigest);
        int randomX = (int)hash&0xFF;
        int randomY = (int)(hash>>8)&0xFF;
        return new Point<>((randomX/255.0)-0.5, (randomY/255.0)-0.5);
    }

    public static long calculateAnimalDistributionValue(final int x, final int y, final int day) {
        MessageDigest messageDigest = createMessageDigest();
        if (messageDigest == null) {
            return 0L;
        }

        long v;
        int b;
        for (int i=0; i<3; i++) {
            switch(i) {
                case 0: v=x; break;
                case 1: v=y; break;
                default: v=day; break;
            }
            for (int j=0; j<4; j++) {
                b = (int)(v&0xFF);
                v = v>>8;
                messageDigest.update((byte)(b&0xFF));
            }
        }
        return getHash(messageDigest);
    }

    public static boolean isHiddenAnimalGift(final long distributionValue) {
        return ((distributionValue%5L)==0L);
    }

    private static MessageDigest createMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static long getHash(final MessageDigest messageDigest) {
        byte hash[] = messageDigest.digest();
        long v = 0L;
        for (int i=0; i<4; i++) {
            v = (v<<8) | (hash[i]&0xFF);
        }
        return v;
    }

    /* http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula */
    public static double CalculateDistance(final Point<Double> p1, final Point<Double> p2) {

        double latDistance = Math.toRadians(p1.getY() - p2.getY());
        double lngDistance = Math.toRadians(p1.getX() - p2.getX());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getY())) * Math.cos(Math.toRadians(p2.getY()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AnimalExchangeApplication.AVERAGE_RADIUS_OF_EARTH * c;
    }

}
