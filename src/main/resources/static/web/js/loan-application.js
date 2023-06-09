const { createApp } = Vue

createApp({
	data() {
		return {
			// Inicializamos las variables
			data: [],
			selectInput: "",
			checked: "",
			amount: "",
			amountModal:0,
			dataFilter: "",
			idLoan: "",
			data2: "",
			account: "",
			interestDay: "",
			amountInterest: 0,
			payments:"",
			quotas: 0,
			activeAccounts: [],
			isAsideInactive: true,
		}
	},
	created() {
		this.dataLoan();
		this.loadData();
	},
	methods: {
		dataLoan() {
			axios.get("/api/loans")
				.then(response => {
					this.data = response.data;
				})
				.catch(error => console.log(error))
		},
		loadData() {
			axios.get('http://localhost:8080/api/clients/current')
				.then(response => {
					this.data2 = response.data;
					this.accounts = this.data2.accounts;
					this.activeAccounts = this.accounts.filter(account => account.accountActive);
				})
				.catch(error => console.log(error));
		},
		filterSelector() {
			this.dataFilter = this.data.filter(loan => {
				return this.checked.includes(loan.name)
			})[0]
		},
		createLoan() {
			this.idLoan = this.dataFilter.id;
			Swal.fire({
				title: 'Are you sure you want a new loan?',
				inputAttributes: {
				},
				showCancelButton: true,
				confirmButtonText: 'Sure',
				preConfirm: () => {
					return axios.post('/api/loans' , {
                        "id" : this.idLoan,
                        "amount" : this.amount,
                        "payments" : this.selectInput,
                        "accountNumber" : this.account
                        })
						.then(response => {
							Swal.fire({
								icon: 'success',
								text: 'Loan succesfully',
								showConfirmButton: false,
								timer: 2000,
							})
						})
						.then(() => window.location.href = "/web/pages/accounts.html")
						.catch(error => {
							Swal.fire({
								icon: 'error',
								text: error.response.data,
							})
						})
				},
				allowOutsideClick: () => !Swal.isLoading()
			})
		},
		logout() {
			axios.post("/api/logout")
				.then(response => window.location.href = "/web/pages/signon.html")
		},
		appearmenu() {
			this.isAsideInactive = !this.isAsideInactive;
		},
		interestRatio(){
            this.amountModal = this.amount;
            this.amountInterest = this.amount * 1.2;
            this.quotas = this.amountInterest / this.selectInput;
			// this.finalAmount = this.amount*this.dataFilter.interes
        },
	},
}).mount("#app");

//boton
const btnScrollTop = document.querySelector('#btn-scroll-top');

btnScrollTop.addEventListener('click', function () {
	window.scrollTo({
		top: 0,
		behavior: 'smooth'
	});
});

window.addEventListener('scroll', function () {
	if (window.pageYOffset > 50) {
		btnScrollTop.style.display = 'block';
	} else {
		btnScrollTop.style.display = 'none';
	}
});

// Obtener referencia al contenedor de loading
const loadingContainer = document.getElementById("loading-container");
function showLoading() {
	loadingContainer.style.display = "flex";
}
function hideLoading() {
	loadingContainer.style.display = "none";
}
showLoading();
window.addEventListener("load", () => {
	hideLoading();
});