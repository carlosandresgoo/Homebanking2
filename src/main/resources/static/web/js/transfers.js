const { createApp } = Vue;

createApp({
	data() {
		return {
			datos: [],
			destinateAccount: "",
            account : "",
            amount : "",
            description : "",
			activeAccounts: [],
			isAsideInactive: true,
		};
	},
	created() {
		this.loadData();
	},
	methods: {
		loadData() {
			axios
				.get('http://localhost:8080/api/clients/current')
				.then(response => {
					this.datos = response.data;
					this.accounts = this.datos.accounts;
					this.activeAccounts = this.accounts.filter(account => account.accountActive);
				})
				.catch(error => console.log(error));
		},
		createTransactions(){
            Swal.fire({
                title: 'Are you sure that you want to transfer this amount to this account',
                inputAttributes: {autocapitalize: 'off'},
                showCancelButton: true,
                confirmButtonText: 'Sure',
                preConfirm: () => {
                    return axios.post('/api/clients/current/transactions', "amount="+ this.amount + "&description=" +  this.description + "&initialAccount=" + this.account + "&destinateAccount=" + this.destinateAccount)
                        .then(response =>
                            Swal.fire({
                                icon: 'success',
                                text: 'Transaction succesfully',
                                showConfirmButton: false,
                                timer: 2000,
                            })
                            .then( () => window.location.href="/web/pages/accounts.html")
                            .catch(error => console.log(error)))
                        .catch(error => {
                            Swal.fire({
                                icon: 'error',
                                text: error.response.data,
                            })
                        })
                },
                allowOutsideClick: () => !Swal.isLoading()
            })
            .catch(error => {console.log(error)})
        },
		logout(){
			axios.post("/api/logout")
			.then(response => window.location.href = "/web/pages/signon.html" )
		},
		appearmenu() {
			this.isAsideInactive = !this.isAsideInactive;
		},
		
	},
	computed: {
        upperCase(){
            this.account = this.account.toUpperCase();
            this.destinateAccount = this.destinateAccount.toUpperCase();
        },
	}
}).mount('#app');

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



