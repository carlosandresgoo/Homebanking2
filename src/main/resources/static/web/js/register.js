
const { createApp } = Vue;

createApp({
    data() {
        return {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            isAsideInactive: true,
            texto: "",

        };
    },
    methods: {
        register() {
            Swal.fire({
                title: 'Are you sure that you want to log out',
                inputAttributes: {
                    autocapitalize: 'off'
                },
                showCancelButton: true,
                confirmButtonText: 'Sure',
                showLoaderOnConfirm: true,
                preConfirm: (login) => {
                    return axios.post('/api/clients', 'firstName=' + this.firstName + '&lastName=' + this.lastName + '&email=' + this.email + "&password=" + this.password)
                        .then(response =>
                            axios.post('/api/login', 'email=' + this.email + "&password=" + this.password)
                                .then(response => window.location.href = "/web/pages/signon.html")
                        )
                        .catch(error => {
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
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
    computed: {
        firstCapital() {
            this.firstName = this.firstName.charAt(0).toUpperCase() + this.firstName.slice(1);
            this.lastName = this.lastName.charAt(0).toUpperCase() + this.lastName.slice(1)
        }
        },
}).mount('#app');

// boton
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