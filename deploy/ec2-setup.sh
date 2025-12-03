echo "Atualizando sistema"
sudo apt-get update && sudo apt-get upgrade -y

echo "Instalando Docker"
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu

echo "Clonando Repositório"
cd /home/ubuntu
git clone https://github.com/SEU_USUARIO/polaris-task-manager.git
cd polaris-task-manager

echo "Subindo Containers"

sudo docker compose up -d --build

echo "Deploy Concluído! Acesse no IP Público da instância."