const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  // Seed Alice's profile
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Rahman",
      email: "alice@example.com",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Rahman",
      email: "alice@example.com",
      phone: "+8801711000000",
      defaultAddress: "Dhanmondi 27, Dhaka",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and read orders under own subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const orderData = {
    userId: ALICE_UID,
    restaurantId: "rest_1",
    restaurantName: "Sultan's Dine",
    itemsCount: 2,
    totalAmount: 950,
    status: "PLACED",
    deliveryAddress: "Banani 11, Dhaka",
    phone: "+8801711223344",
    paymentMethod: "Cash on Delivery",
    createdAt: new Date(),
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("order_001").set(orderData)
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("order_001").get()
  );

  // Bob cannot read Alice's order
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("orders").doc("order_001").get()
  );
});

test("Authenticated user: can create and read favorites under own subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const favData = {
    userId: ALICE_UID,
    restaurantId: "rest_1",
    restaurantName: "Sultan's Dine",
    cuisine: "Kacchi Biryani",
    rating: 4.8,
    createdAt: new Date(),
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("favorites").doc("fav_001").set(favData)
  );

  // Bob cannot read Alice's favorites
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("favorites").doc("fav_001").get()
  );
});
