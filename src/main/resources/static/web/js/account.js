const {createApp} = Vue;

createApp({
	data() {
		return {
			account: [],
			transactions: [],
			type:"",
			description:"",
			totalBalance: 0,
			startDate: "",
			endDate: "",
			isAsideInactive: true,
			id: new URLSearchParams(location.search).get('id'),
		};
	},
	created() {
		this.loadData();
	},
	methods: {
		loadData() {
			axios
				.get('http://localhost:8080/api/clients/current/accounts/' + this.id)
				.then(response => {
					this.account = response.data;
					this.transactions = this.account.transactions;
					this.transactions.sort((a, b) => {
						return new Date(b.date) - new Date(a.date);
					});
					

		})
				.catch(error => console.log(error));
		},
        downloadPDF(){
            Swal.fire({
                title: 'Confirm that you want to download your transactions in PDF',
                inputAttributes: {
                    autocapitalize: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Sure',
                preConfirm: () => {
                    return axios.post('/accounts/transactions/pdf',`id=${this.id}&startDate=${this.startDate}&endDate=${this.endDate}`)
                    .then(response => {
                        Swal.fire({
                            icon: 'success',
                            text: 'Please search in your documents',
                            showConfirmButton: false,
                            timer: 3000,
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
		logout(){
			axios.post("/api/logout")
			.then(response => window.location.href = "/web/pages/signon.html" )

		},
		appearmenu() {
            this.isAsideInactive = !this.isAsideInactive;
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