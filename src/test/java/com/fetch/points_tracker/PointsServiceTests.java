package com.fetch.points_tracker;

import com.fetch.points_tracker.exceptions.InsufficientPointsException;
import com.fetch.points_tracker.exceptions.InvalidTransactionException;
import com.fetch.points_tracker.service.PointsService;
import com.fetch.points_tracker.model.Transaction;
import com.fetch.points_tracker.repository.PointsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsServiceTest {

	private PointsRepository pointsRepository;
	private PointsService pointsService;

	@BeforeEach
	void setUp() {
		pointsRepository = mock(PointsRepository.class);
		pointsService = new PointsService(pointsRepository);
	}

	@Test
	void addPoints_ValidTransaction_ShouldSaveTransactionAndUpdateBalance() {
		Transaction transaction = new Transaction("DANNON", 1000, ZonedDateTime.now());

		pointsService.addPoints(transaction);

		verify(pointsRepository, times(1)).saveTransaction(transaction);
		verify(pointsRepository, times(1)).updateBalance("DANNON", 1000);
	}

	@Test
	void addPoints_InvalidTransaction_ShouldThrowException() {
		Transaction invalidTransaction = new Transaction("", 0, null);

		assertThrows(InvalidTransactionException.class, () -> pointsService.addPoints(invalidTransaction));

		verify(pointsRepository, never()).saveTransaction(any());
		verify(pointsRepository, never()).updateBalance(anyString(), anyInt());
	}

	@Test
	void spendPoints_ValidRequest_ShouldDeductPointsAndRetainZeroBalances() {
		List<Transaction> mockTransactions = List.of(
				new Transaction("DANNON", 300, ZonedDateTime.parse("2022-10-31T10:00:00Z")),
				new Transaction("UNILEVER", 200, ZonedDateTime.parse("2022-10-31T11:00:00Z")),
				new Transaction("MILLER COORS", 10000, ZonedDateTime.parse("2022-11-01T14:00:00Z"))
		);

		// Mock repository responses
		when(pointsRepository.getAllTransactions()).thenReturn(new ArrayList<>(mockTransactions));
		when(pointsRepository.getBalance()).thenReturn(10500); // Total points in the mock transactions
		when(pointsRepository.getBalanceByPayer("DANNON")).thenReturn(300);
		when(pointsRepository.getBalanceByPayer("UNILEVER")).thenReturn(200);
		when(pointsRepository.getBalanceByPayer("MILLER COORS")).thenReturn(10000);

		List<Map<String, Object>> spent = pointsService.spendPoints(5000);

		verify(pointsRepository, times(1)).updateBalance("DANNON", -300);
		verify(pointsRepository, times(1)).updateBalance("UNILEVER", -200);
		verify(pointsRepository, times(1)).updateBalance("MILLER COORS", -4500);

		// Assert spent points
		assertEquals(3, spent.size());
		assertEquals("DANNON", spent.get(0).get("payer"));
		assertEquals(-300, spent.get(0).get("points"));
		assertEquals("UNILEVER", spent.get(1).get("payer"));
		assertEquals(-200, spent.get(1).get("points"));
		assertEquals("MILLER COORS", spent.get(2).get("payer"));
		// Ensure zero-point transactions are not removed
		verify(pointsRepository, never()).deleteTransactionsByZeroPoints();
	}

	@Test
	void spendPoints_NotEnoughPoints_ShouldThrowException() {
		List<Transaction> mockTransactions = List.of(
				new Transaction("DANNON", 300, ZonedDateTime.parse("2022-10-31T10:00:00Z"))
		);

		when(pointsRepository.getAllTransactions()).thenReturn(new ArrayList<>(mockTransactions));

		assertThrows(InsufficientPointsException.class, () -> pointsService.spendPoints(500));

		verify(pointsRepository, never()).updateBalance(anyString(), anyInt());
	}

	@Test
	void getBalances_ShouldReturnCorrectBalancesWithZeroPoints() {
		List<Transaction> mockTransactions = List.of(
				new Transaction("DANNON", 300, ZonedDateTime.parse("2022-10-31T10:00:00Z")),
				new Transaction("UNILEVER", 0, ZonedDateTime.parse("2022-10-31T11:00:00Z")),
				new Transaction("MILLER COORS", 10000, ZonedDateTime.parse("2022-11-01T14:00:00Z"))
		);

		when(pointsRepository.getAllTransactions()).thenReturn(mockTransactions);
		when(pointsRepository.getBalanceByPayer("DANNON")).thenReturn(300);
		when(pointsRepository.getBalanceByPayer("UNILEVER")).thenReturn(0);
		when(pointsRepository.getBalanceByPayer("MILLER COORS")).thenReturn(10000);

		Map<String, Integer> balances = pointsService.getBalances();

		// Assert balances
		assertEquals(3, balances.size());
		assertEquals(300, balances.get("DANNON"));
		assertEquals(0, balances.get("UNILEVER")); // Verify zero balance is kept
		assertEquals(10000, balances.get("MILLER COORS"));
	}

	@Test
	void spendPoints_ShouldDeductPointsAcrossMultiplePayers() {
		List<Transaction> mockTransactions = new ArrayList<>(Arrays.asList(
				new Transaction("DANNON", 300, ZonedDateTime.parse("2022-10-31T10:00:00Z")),
				new Transaction("DANNON", 200, ZonedDateTime.parse("2022-10-31T15:00:00Z")),
				new Transaction("MILLER COORS", 10000, ZonedDateTime.parse("2022-11-01T14:00:00Z"))
		));

		when(pointsRepository.getAllTransactions()).thenReturn(mockTransactions);
		when(pointsRepository.getBalance()).thenReturn(10500); // Total points in the mock transactions
		when(pointsRepository.getBalanceByPayer("DANNON")).thenReturn(500);
		when(pointsRepository.getBalanceByPayer("MILLER COORS")).thenReturn(10000);

		List<Map<String, Object>> spent = pointsService.spendPoints(1500);

		verify(pointsRepository, times(1)).updateBalance("DANNON", -300);
		verify(pointsRepository, times(1)).updateBalance("DANNON", -200);
		verify(pointsRepository, times(1)).updateBalance("MILLER COORS", -1000);

		// Assert spent points
		assertEquals(3, spent.size());
		assertEquals("DANNON", spent.get(0).get("payer"));
		assertEquals(-300, spent.get(0).get("points"));
		assertEquals("DANNON", spent.get(1).get("payer"));
		assertEquals(-200, spent.get(1).get("points"));
		assertEquals("MILLER COORS", spent.get(2).get("payer"));
		assertEquals(-1000, spent.get(2).get("points"));
	}
}

