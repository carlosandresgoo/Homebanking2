const { createApp } = Vue

createApp({
	data() {
		return {
			// Inicializamos las variables
			data: [],
            loans: [],
			selectInput: "",
			checked: "",
			amount: 0,
			dataFilter: "",
			idLoan: "",
			data2: "",
			account: "",
			interestDay: "",
			amountInterest: 0,
			quotas: 0,
            totalPay: 0,
			isAsideInactive: true,
		}
	},
	created() {
		this.loadData();
	},
	methods: {
        loadData() {
            axios.get('http://localhost:8080/api/clients/current')
                .then(response => {
                    console.log(response.data);

                    this.data = response.data
                    this.loans = this.data.loans.filter(loan => loan.finalAmount > 0);
                    console.log(this.loans);
                    this.accounts = this.data.accounts

                    for (account of this.data.accounts) {
                        this.totalBalance += account.balance;
                    }
                })
                .catch(error => console.log(error));
        },
		filterLoan(event){
            this.dataFilter = this.loans.filter( loan => {
                return event.target.alt.includes(loan.name)
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
                confirmButtonColor: "#7c601893",
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
                                confirmButtonColor: "#7c601893",
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