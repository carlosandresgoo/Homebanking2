
const { createApp } = Vue;

createApp({
	data() {
		return {
			isAsideInactive: true,
			email: "",
			password: "",

		};
	},
	methods: {
		signin() {
			axios.post('/api/login', "email=" + this.email + "&password=" + this.password)
				.then(response => {
					Swal.fire({
						position: 'center',
						icon: 'success',
						title: 'login successfully',
						showConfirmButton: false,
						timer: 2000,
					}).then(function(){
						window.location.href = "/web/pages/accounts.html"
					})
					
				})
				.catch(error => {
					Swal.fire({
						icon: 'error',
						title: 'Oops...',
						text: 'incorrect user data!',

					})
					console.log(error)
				});
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