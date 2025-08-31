package co.com.crediya.application.usecase.createapplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DataUtils {
  private DataUtils() {}

  private static final Random RANDOM = new SecureRandom();
  private static final String EMAIL_DOMAIN = "@example.com";
  private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER = LETTERS.toUpperCase(Locale.ROOT);
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL = "!@#$%^&*()-_=+<>?/";
  private static final String COL_CODE = "57";

  public static String randomEmail() {
    String prefix = UUID.randomUUID().toString().substring(0, 8);
    return prefix + EMAIL_DOMAIN;
  }

  public static String randomName() {
    int length = RANDOM.nextInt(5, 12);
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toUpperCase(LETTERS.charAt(RANDOM.nextInt(LETTERS.length()))));
    for (int i = 1; i < length; i++) {
      sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
    }
    return sb.toString();
  }

  public static LocalDate now() {
    return LocalDate.now();
  }

  public static String randomAddress() {
    return randomEmail();
  }

  public static String randomPassword(int length) {
    if (length < 6) {
      throw new IllegalArgumentException("Password length must be at least 6");
    }
    StringBuilder sb = new StringBuilder();

    sb.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
    sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
    sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
    sb.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

    String allChars = UPPER + LETTERS + DIGITS + SPECIAL;
    for (int i = 4; i < length; i++) {
      sb.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
    }

    return sb.toString();
  }

  public static boolean randomBoolean() {
    return RANDOM.nextBoolean();
  }

  public static int randomInt(int min, int max) {
    return RANDOM.nextInt(max - min + 1) + min;
  }

  public static String randomPhone() {
    int prefix = 300 + RANDOM.nextInt(30);
    long number = 1_000_000L + RANDOM.nextInt(9_000_000);
    return COL_CODE + prefix + number;
  }

  public static String randomIdNumber() {
    int length = 8 + RANDOM.nextInt(5);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(RANDOM.nextInt(10));
    }
    return sb.toString();
  }

  public static String randomIdType() {
    return RANDOM.nextBoolean() ? "CC" : "PASSPORT";
  }

  public static BigDecimal randomBigDecimal() {
    long value = RANDOM.nextLong(1_000_000L, 200_000_000L);
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
