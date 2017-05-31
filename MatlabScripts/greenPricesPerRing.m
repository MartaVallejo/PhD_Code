function greenPricesPerRing()
%Plot average green price prices grouped by rings
% In which tick of the clock?

cd('../General');
E = load('Rings.txt');

X = E(1,:);
steps = [1:1:size(X,2)];

hh=plot(steps, X);
set (hh, 'LineWidth', 2)
hold on;
xlabel('Rings','fontweight','bold','fontsize',14)
ylabel('Average Price','fontweight','bold','fontsize',14)
title('Evolution of the rural prices','fontweight','bold','fontsize',16)
handle=gcf;
saveas(handle, ['rings', 'png'],'png')
hold off;
cd('../Functions');
end

