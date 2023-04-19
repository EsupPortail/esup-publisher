<template>
  <div class="content list-group">
    <router-link to="/publish/publisher" class="list-group-item">
      <div id="publish-publisher-item" class="bouton-action">
        <div class="div-icon text-end">
          <i
            class="fas fa-pencil-alt fa-5x d-none d-sm-none d-md-inline-block"
          ></i>
          <i
            class="fas fa-pencil-alt fa-2x d-inline-block d-sm-inline-block d-md-none"
          ></i>
        </div>
        <div class="div-text text-start">
          <h4 class="item-heading">{{ $t("main.link.write.name") }}</h4>
          <span class="item-text d-none d-sm-none d-md-block">{{
            $t("main.link.write.desc")
          }}</span>
        </div>
      </div>
    </router-link>

    <router-link to="/contents/owned/DRAFT" class="list-group-item">
      <div id="owned-item" class="bouton-action">
        <div class="div-icon text-end">
          <i
            class="fas fa-file-alt fa-5x d-none d-sm-none d-md-inline-block"
          ></i>
          <i
            class="fas fa-file-alt fa-2x d-inline-block d-sm-inline-block d-md-none"
          ></i>
        </div>
        <div class="div-text text-start">
          <h4 class="item-heading">{{ $t("main.link.published.name") }}</h4>
          <span class="item-text d-none d-sm-none d-md-block">{{
            $t("main.link.published.desc")
          }}</span>
        </div>
      </div>
    </router-link>

    <router-link
      to="/contents/pending"
      class="list-group-item"
      v-if="canModerate"
    >
      <div id="pending-item" class="bouton-action">
        <div class="div-icon text-end">
          <i class="fas fa-gavel fa-5x d-none d-sm-none d-md-inline-block"></i>
          <i
            class="fas fa-gavel fa-2x d-inline-block d-sm-inline-block d-md-none"
          ></i>
        </div>
        <div class="div-text text-start">
          <h4 class="item-heading">{{ $t("main.link.moderate.name") }}</h4>
          <span class="item-text d-none d-sm-none d-md-block">{{
            $t("main.link.moderate.desc")
          }}</span>
        </div>
      </div>
    </router-link>

    <router-link to="/treeview" class="list-group-item" v-if="canModerate">
      <div id="treeview-item" class="bouton-action">
        <div class="div-icon text-end">
          <i class="fas fa-wrench fa-5x d-none d-sm-none d-md-inline-block"></i>
          <i
            class="fas fa-wrench fa-2x d-inline-block d-sm-inline-block d-md-none"
          ></i>
        </div>
        <div class="div-text text-start">
          <h4 class="item-heading">{{ $t("main.link.manage.name") }}</h4>
          <span class="item-text d-none d-sm-none d-md-block">{{
            $t("main.link.manage.desc")
          }}</span>
        </div>
      </div>
    </router-link>

    <router-link
      to="/administration"
      class="list-group-item"
      v-has-role="'ROLE_ADMIN'"
    >
      <div id="administration-item" class="bouton-action">
        <div class="div-icon text-end">
          <i class="fas fa-cogs fa-5x d-none d-sm-none d-md-inline-block"></i>
          <i
            class="fas fa-cogs fa-2x d-inline-block d-sm-inline-block d-md-none"
          ></i>
        </div>
        <div class="div-text text-start">
          <h4 class="item-heading">{{ $t("main.link.admin.name") }}</h4>
          <span class="item-text d-none d-sm-none d-md-block">{{
            $t("main.link.admin.desc")
          }}</span>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script>
import UserService from "@/services/user/UserService";

export default {
  name: "Home",
  data() {
    return {
      canModerate: false,
    };
  },
  created() {
    UserService.canModerateAnyThing().then((response) => {
      this.canModerate = response.data.value;
    });
  },
};
</script>
