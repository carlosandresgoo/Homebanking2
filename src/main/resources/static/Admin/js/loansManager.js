const { createApp } = Vue;

createApp({
    data() {
        return {
            name: "",
            amount: "",
            checked: [],
            description: "",
            interest: "",
            isAsideInactive: true,
        }
    },
    methods: {
        createLoan(){
            Swal.fire({
                title: 'Are you sure that you want to create this loan?',
                inputAttributes: {
                    autocapitalize: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Sure',
                preConfirm: () => {
                    return axios.post('/api/loans/manager' , {
                        "name" : this.name,
                        "maxAmount" : this.amount,
                        "payments" : this.checked,
                        "interest" : this.interest
                        })
                        .then(response => {
                            Swal.fire({
                                icon: 'success',
                                text: 'You create a new Loan',
                                showConfirmButton: false,
                                timer: 2000,
                            }).then( () => window.location.href="/admin/pages/loansManager.html")
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