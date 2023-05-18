const { createApp } = Vue;

createApp({
	data() {
		return {
			datos: [],
			cards: [],
			debit: [],
			credit: [],
			activeCards: [],
			isAsideInactive: true,
			dateNow: new Date().toLocaleString().split(',')[0].split('/').reverse().join('-'),
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
					this.cards = this.datos.cards;
					this.credit = this.datos.cards.filter(card => card.type == "CREDIT" && card.cardActive);
					console.log(this.credit)
					this.debit = this.datos.cards.filter(card => card.type == "DEBIT" && card.cardActive);
					this.activeCards = this.cards.filter(card => card.cardActive);
				})
				.catch(error => console.log(error));
		},
		logout() {
			axios.post("/api/logout")
				.then(response => window.location.href = "/web/pages/signon.html")
		},
		appearmenu() {
			this.isAsideInactive = !this.isAsideInactive;
		},
		deleteCard(id) {
			Swal.fire({
				title: 'Are you sure you want to delete card?',
				inputAttributes: { autocapitalize: 'off' },
				showCancelButton: true,
				confirmButtonText: 'Sure',
				preConfirm: () => {
					axios.put(`/api/cards/${id}`)
						.then(response =>
							Swal.fire({
								icon: 'success',
								text: 'Card deletion successful',
								showConfirmButton: false,
								timer: 2000,
							})
								.then(() => window.location.href = "/web/pages/cards.html")
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



