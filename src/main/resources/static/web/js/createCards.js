const { createApp } = Vue;

createApp({
	data() {
		return {
            type:"",
            color:"",
			isAsideInactive: true,
		};
	},
	methods: {
	
		logout(){
			axios.post("/api/logout")
			.then(response => window.location.href = "/web/pages/signon.html" )
		},
        createcard() {
            Swal.fire({
                title: 'Are you sure you want to create a new card?',
                inputAttributes: {
                    autocapitalize: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Sure',
                showLoaderOnConfirm: true,
                preConfirm: () => {
                    return axios.post('/api/clients/current/cards', 'type=' + this.type + '&color=' + this.color )
                        .then(response => window.location.href = "/web/pages/cards.html")
                        .catch(error => {
                            Swal.fire({
                                icon: 'error',
                                text: error.response.data,

                            })
                            console.log(error)
                        })

                        .catch(error => {
                            Swal.showValidationMessage(
                                `Request failed: ${error}`
                            )
                        })
                },
                allowOutsideClick: () => !Swal.isLoading()
            })
        },

		appearmenu() {
			this.isAsideInactive = !this.isAsideInactive;
		},
		
	},
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



