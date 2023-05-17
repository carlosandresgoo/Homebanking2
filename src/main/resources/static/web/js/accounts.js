const { createApp } = Vue;

createApp({
	data() {
		return {
			datos: [],
			loans: [],
			firstname: '',
			lastName: '',
			email: '',
			accounts: [],
			params: "",
            id: "",
			dataFilter: 0,
            quotas: 0,
            account: "",
			amount: "",
            totalPay: 0,
			loansFilter:[],
			activeAccounts: [],
			accountType:[],
			totalBalance: 0,
			isAsideInactive: true,
		};
	},
	created() {
		this.loadData();
	},
	methods: {
		loadData() {
			axios.get('http://localhost:8080/api/clients/current')
				.then(response => {
					this.datos = response.data;
					this.accounts = this.datos.accounts;
					this.loans = this.datos.loans;
					this.activeAccounts = this.accounts.filter(account => account.accountActive);
					this.loansFilter = this.datos.loans.filter(loan => loan.finalAmount > 0);
				})
				.catch(error => console.log(error));
		},
		logout() {
			axios.post("/api/logout")
				.then(response => window.location.href = "/web/pages/signon.html")
		},
		createAccount() {
			Swal.fire({
				title: 'Are you sure you want to create a new account?',
				inputAttributes: {
					autocapitalize: 'off'
				},
				showCancelButton: true,
				confirmButtonText: 'Sure',
				showLoaderOnConfirm: true,
				preConfirm: () => {
					return axios.post('/api/clients/current/accounts',`accountType=${this.accountType}`)
						.then(response => window.location.href = "/web/pages/accounts.html")
						.catch(error => {
							Swal.fire({
								icon: 'error',
								text: error.response.data,
							})
							console.log(error)
						})
				},
				
			})
		},
		deleteAccount(id) {
			Swal.fire({
				title: 'Are you sure you want to delete card?',
				inputAttributes: { autocapitalize: 'off' },
				showCancelButton: true,
				confirmButtonText: 'Sure',
				preConfirm: () => {
					axios.put(`/api/accounts/${id}`)
						.then(response =>
							Swal.fire({
								icon: 'success',
								text: 'card deletion successful',
								showConfirmButton: false,
								timer: 2000,
							})
								.then(() => window.location.href = "/web/pages/accounts.html")
								.catch(error => console.log(error)))
						.catch(error => {
							Swal.fire({
								icon: 'error',
								text: error.response.data,
							})
						})
				}
			})
		},
		appearmenu() {
			this.isAsideInactive = !this.isAsideInactive;
		},
		filterLoan(name){
            this.dataFilter = this.loansFilter.filter( loan => {
                return name.includes(loan.name)
            })[0]
            console.log(this.dataFilter);
            this.quotas = this.dataFilter.finalAmount / this.dataFilter.payments;
            this.totalPay = this.dataFilter.finalAmount;
        },
        payLoan(){
            Swal.fire({
                title: 'Are you sure that you want to pay the loan?',
                inputAttributes: {
                    autocapitalize: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Sure',
                preConfirm: () => {
                    return axios.post('/api/current/loans', `idLoan=${this.dataFilter.id}&account=${this.account}&amount=${this.amount}`)
                    .then(response => {
                            Swal.fire({
                                icon: 'success',
                                text: 'Payment Success',
                                showConfirmButton: false,
                                timer: 2000,
                            }).then( () => window.location.href="/web/pages/accounts.html")
                        })
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

	},
}).mount('#app');

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

// loading//
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



