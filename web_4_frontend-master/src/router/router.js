import {createRouter, createWebHistory} from "vue-router"
import store from "@/js/authorization_process.js";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: 'startPage',
            component: () => import("@/view/PageAuthorization.vue")
        },
        {
            path: "/auth",
            name: 'authPage',
            component: () => import("@/view/PageAuthorization.vue")
        },
        {
            path: "/main",
            name: "mainPage",
            component: () => import("@/view/PageMain.vue"),
            meta: {
                reqiresAuth: true
            },
        }
    ]
})
router.beforeEach((to, from, next) => {
    // console.log(to)
    if (to.matched.some(record => record.meta.reqiresAuth)) {
        if (localStorage.getItem("exp_date") > Date.now()) {
            store.commit('auth/changeUserStatus', true)
            next()
            return
        }
        next("/auth")
    } else {
        next()
    }
})
export default router;