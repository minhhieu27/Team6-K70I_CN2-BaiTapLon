package test;

import client.network.AuctionClient;

public class ClientNetworkTest {
    public static void main(String[] args) throws InterruptedException {
        AuctionClient client = new AuctionClient();

        boolean connected = client.connect("localhost", 9999);

        if (!connected) {
            System.out.println("Cannot connect to server");
            return;
        }

        Thread.sleep(500);

        System.out.println("===== TEST LOGIN =====");
        client.login("bach", "123456");

        Thread.sleep(1000);

        System.out.println("===== TEST GET AUCTIONS =====");
        client.getAuctions();

        Thread.sleep(1000);

        System.out.println("===== TEST JOIN AUCTION =====");
        client.joinAuction("A001");

        Thread.sleep(1000);

        System.out.println("===== TEST GET AUCTION DETAIL =====");
        client.getAuctionDetail("A001");

        Thread.sleep(1000);

        System.out.println("===== TEST PLACE BID =====");
        client.placeBid("A001", "U001", 500000);

        Thread.sleep(3000);

        // Tạm thời chưa disconnect để còn xem realtime response
        // client.disconnect();
    }
}