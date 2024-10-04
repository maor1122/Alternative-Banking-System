package com.example.ABSServer;

import com.example.ABSServer.Servlets.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class AbsServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AbsServerApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<AddLoansServlet> addLoansServlet() {
		return new ServletRegistrationBean<>(new AddLoansServlet(), "/abs/add_loans");
	}

	@Bean
	public ServletRegistrationBean<BankServlet> bankServlet() {
		return new ServletRegistrationBean<>(new BankServlet(), "/abs/bank");

	}

	@Bean
	public ServletRegistrationBean<BuyLoanOwnershipServlet> buyLoanOwnershipServlet() {
		return new ServletRegistrationBean<>(new BuyLoanOwnershipServlet(), "/abs/buy_loan");
	}

	@Bean
	public ServletRegistrationBean<ChargeServlet> chargeServlet() {
		return new ServletRegistrationBean<>(new ChargeServlet(), "/abs/charge");
	}

	@Bean
	public ServletRegistrationBean<DecreaseYazSevlet> decreaseYazServlet() {
		return new ServletRegistrationBean<>(new DecreaseYazSevlet(), "/abs/decrease_yaz");
	}

	@Bean
	public ServletRegistrationBean<IncreaseYazServlet> increaseYazServlet() {
		return new ServletRegistrationBean<>(new IncreaseYazServlet(), "/abs/increase_yaz");
	}

	@Bean
	public ServletRegistrationBean<InvestInLoansServlet> investInLoansServlet() {
		return new ServletRegistrationBean<>(new InvestInLoansServlet(), "/abs/invest");
	}

	@Bean
	public ServletRegistrationBean<LightweightLoginServlet> lightweightLoginServlet() {
		return new ServletRegistrationBean<>(new LightweightLoginServlet(), "/abs/login");
	}

	@Bean
	public ServletRegistrationBean<PayDebtServlet> payDebtServlet() {
		return new ServletRegistrationBean<>(new PayDebtServlet(), "/abs/pay_debt");
	}

	@Bean
	public ServletRegistrationBean<PayLoanServlet> payLoanServlet() {
		return new ServletRegistrationBean<>(new PayLoanServlet(), "/abs/pay_loan");
	}

	@Bean
	public ServletRegistrationBean<PutLoanOwnershipForSaleSevlet> putLoanOwnershipForSaleServlet() {
		return new ServletRegistrationBean<>(new PutLoanOwnershipForSaleSevlet(), "/abs/loan_sale");
	}

	@Bean
	public ServletRegistrationBean<RewindServlet> rewindServlet() {
		return new ServletRegistrationBean<>(new RewindServlet(), "/abs/rewind");
	}

	@Bean
	public ServletRegistrationBean<WithdrawServlet> withdrawServlet() {
		return new ServletRegistrationBean<>(new WithdrawServlet(), "/abs/withdraw");
	}
}
