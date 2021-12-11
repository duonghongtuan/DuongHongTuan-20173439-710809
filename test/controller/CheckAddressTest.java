package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CheckAddressTest {

private PlaceRushOrderController placeRushOrderController;
	
	@BeforeEach
	void setUp() throws Exception {
		placeRushOrderController = new PlaceRushOrderController();
	}
	@ParameterizedTest
	@CsvSource({
		"Hà Nội tuan,true",
		"so 15 hai ba trung ha noi tuan,true",
		"thai nguyen,false"
	})
	
	void test(String address, boolean expected) {
		boolean isValid = placeRushOrderController.checkAddress(address);
		assertEquals(expected,isValid);
	}

}
